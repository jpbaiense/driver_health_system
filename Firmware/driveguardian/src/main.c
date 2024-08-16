/*
      Main Source File
      Author: João Pedro Baiense
      Date: 21/04/2024
*/

#include <zephyr/kernel.h>
#include <zephyr/drivers/gpio.h>
#include <zephyr/logging/log.h>
#include <zephyr/drivers/i2c.h>
#include <zephyr/bluetooth/bluetooth.h>
#include <zephyr/bluetooth/gap.h>
#include <zephyr/bluetooth/uuid.h>
#include <zephyr/bluetooth/conn.h>
#include <dk_buttons_and_leds.h>
#include "bh1790glc.h"
#include "lis2dw12tr.h"
#include "sensor_data.h"
#include "bmp581.h"
#include "max17043.h"

// The "foundries_io_mod" logger module level is set to "debug" (numeric level 4).
#define LOG_LEVEL CONFIG_FOUNDRIES_IO_MOD_LOG_LEVEL
#define LOG_MODULE_NAME foundries_io_mod
#include <zephyr/logging/log.h>
LOG_MODULE_REGISTER();

static struct bt_le_adv_param *adv_param = BT_LE_ADV_PARAM(
	(BT_LE_ADV_OPT_CONNECTABLE |
	 BT_LE_ADV_OPT_USE_IDENTITY), /* Connectable advertising and use identity address */
	800, /* Min Advertising Interval 500ms (800*0.625ms) */
	801, /* Max Advertising Interval 500.625ms (801*0.625ms) */
	NULL); /* Set to NULL for undirected advertising */

#define DEVICE_NAME "DRIVE_GUARDIAN"
#define DEVICE_NAME_LEN (sizeof(DEVICE_NAME) - 1)

#define STACKSIZE 1024
#define PRIORITY 0
/* Interval for run time */
#define RUN_INTERVAL 1000
/* Interval to send data over BLE */
#define NOTIFY_INTERVAL 500

static const struct bt_data ad[] = {
	BT_DATA_BYTES(BT_DATA_FLAGS, (BT_LE_AD_GENERAL | BT_LE_AD_NO_BREDR)),
	BT_DATA(BT_DATA_NAME_COMPLETE, DEVICE_NAME, DEVICE_NAME_LEN),
};

static const struct bt_data sd[] = {
	BT_DATA_BYTES(BT_DATA_UUID128_ALL, BT_UUID_DRIVE_GUARDIAN_VAL),
};

/* Struct that stores the data to stream over Bluetooth LE */
SensorData sensor_data;

float vcell;
uint16_t ppg_led_off;

/* thread function  */
void send_data_thread(void)
{
	while (1) {
		int err;

		err = BH1790GLC_get_val(&ppg_led_off, &sensor_data.ppg_led_on);

		if(err != SUCCESS){
			LOG_INF("Couldn't get PPG value!\n\r");
		}

		LOG_INF("PPG values retrieved, OFF: %d and ON: %d \n\r", ppg_led_off, sensor_data.ppg_led_on);

		sensor_data.x_axis_data = get_x_axes_data();

		sensor_data.y_axis_data = get_y_axes_data();

		sensor_data.z_axis_data = get_z_axes_data();

		LOG_INF("X axis data: %d, Y axis data: %d and Z axis data: %d\n\r", sensor_data.x_axis_data, sensor_data.y_axis_data, sensor_data.z_axis_data); 
		
		err = BMP581_get_temp_press_val(&sensor_data.temperature, &sensor_data.pressure);
	
		if(err != SUCCESS){
			LOG_INF("Couldn't get pressure value!\n\r");
		}

		LOG_INF("temperature value: %d and pressure value %d \n\r", sensor_data.temperature, sensor_data.pressure);

		err = max17043_get_vcell(&vcell);

		if(err != SUCCESS){
			LOG_INF("Couldn't get VCELL data!\n\r");
		}

		LOG_INF("vcell value: %f \n\r", vcell);
		
		err = max17043_get_soc(&sensor_data.soc);

		if(err != SUCCESS){
			LOG_INF("Couldn't get SOC data!\n\r");
		}
		
		LOG_INF("SOC value: %f \n\r", sensor_data.soc);	
		
		/* Send notification, the function sends notifications only if a client is subscribed */
		send_sensor_notify(&sensor_data);

		k_sleep(K_MSEC(NOTIFY_INTERVAL));
	}
}

static void on_connected(struct bt_conn *conn, uint8_t err)
{
	if (err) {
		printk("Connection failed (err %u)\n", err);
		return;
	}

	printk("Connected\n");
}

static void on_disconnected(struct bt_conn *conn, uint8_t reason)
{
	printk("Disconnected (reason %u)\n", reason);
}

struct bt_conn_cb connection_callbacks = {
	.connected = on_connected,
	.disconnected = on_disconnected,
};

void main(void)
{
	int err;

	err = BH1790GLC_init();

	if(err != 0){
		LOG_INF("Couldn't load PPG sensor!\n\r");
	} else {
		LOG_INF("PPG sensor loaded!\n\r");
	}

	err = lis2dw12_init();

	if(err != 0){
		LOG_INF("Couldn't load ACC sensor!\n\r");
	} else {
		LOG_INF("ACC sensor loaded!\n\r");
	}

	err = bmp581_init();

	if(err != 0){
		LOG_INF("Couldn't load pressure sensor!\n\r");
	} else {
		LOG_INF("pressure sensor loaded!\n\r");
	}

	err = max17043_init();

	if(err != 0){
		LOG_INF("Couldn't load fuel gauge!\n\r");
	} else {
		LOG_INF("Fuel gauge loaded!\n\r");
	}
	
	err = bt_enable(NULL);
	if (err) {
		LOG_ERR("Bluetooth init failed (err %d)\n", err);
		return;
	}
	
	bt_conn_cb_register(&connection_callbacks);

	LOG_INF("Bluetooth initialized\n");
	err = bt_le_adv_start(adv_param, ad, ARRAY_SIZE(ad), sd, ARRAY_SIZE(sd));
	if (err) {
		LOG_ERR("Advertising failed to start (err %d)\n", err);
		return;
	}

	LOG_INF("Advertising successfully started\n");
	for (;;) {
		k_sleep(K_MSEC(RUN_INTERVAL));
	}
}
/* Define and initialize a thread to send data periodically */
K_THREAD_DEFINE(send_data_thread_id, STACKSIZE, send_data_thread, NULL, NULL, NULL, PRIORITY, 0, 0);