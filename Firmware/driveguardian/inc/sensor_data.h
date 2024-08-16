/*
      Sensor Data
      Author: João Pedro Baiense
      Date: 21/04/2024
      based on: 
      https://academy.nordicsemi.com/

    * Copyright (c) 2018 Nordic Semiconductor ASA
    *
    * SPDX-License-Identifier: LicenseRef-Nordic-5-Clause
*/

#include <zephyr/types.h>
#include <stddef.h>
#include <string.h>
#include <errno.h>
#include <zephyr/sys/printk.h>
#include <zephyr/sys/byteorder.h>
#include <zephyr/kernel.h>
#include <zephyr/logging/log.h>
#include <zephyr/bluetooth/bluetooth.h>
#include <zephyr/bluetooth/hci.h>
#include <zephyr/bluetooth/conn.h>
#include <zephyr/bluetooth/uuid.h>
#include <zephyr/bluetooth/gatt.h>
#include <zephyr/types.h>

/**@file
 * @defgroup bt_DRIVE_GUARDIAN Service API
 * @{
 * @brief API for the Bluetooth Low Energy Drive Guardian (DRIVE_GUARDIAN).
 */

/** @brief DRIVE_GUARDIAN Service UUID. */
#define BT_UUID_DRIVE_GUARDIAN_VAL BT_UUID_128_ENCODE(0x6ab7e5ba, 0xfb0a, 04253, 0xa6ec, 0x7801217ca578)

/** @brief Sensor Data Characteristic UUID. */
#define BT_UUID_DRIVE_GUARDIAN_MYSENSOR_VAL  BT_UUID_128_ENCODE(0x6ab7e5bb, 0xfb0a, 04253, 0xa6ec, 0x7801217ca578)

#define BT_UUID_DRIVE_GUARDIAN BT_UUID_DECLARE_128(BT_UUID_DRIVE_GUARDIAN_VAL)
/* STEP 11.2 - Convert the array to a generic UUID */
#define BT_UUID_DRIVE_GUARDIAN_MYSENSOR BT_UUID_DECLARE_128(BT_UUID_DRIVE_GUARDIAN_MYSENSOR_VAL)


/* Additional Characteristics */

/** @brief Button Characteristic UUID. */
#define BT_UUID_DRIVE_GUARDIAN_BUTTON_VAL                                                                     \
	BT_UUID_128_ENCODE(0x00001524, 0x1212, 0xefde, 0x1523, 0x785feabcd123)

/** @brief LED Characteristic UUID. */
#define BT_UUID_DRIVE_GUARDIAN_LED_VAL BT_UUID_128_ENCODE(0x00001525, 0x1212, 0xefde, 0x1523, 0x785feabcd123)

#define BT_UUID_DRIVE_GUARDIAN_BUTTON BT_UUID_DECLARE_128(BT_UUID_DRIVE_GUARDIAN_BUTTON_VAL)
#define BT_UUID_DRIVE_GUARDIAN_LED BT_UUID_DECLARE_128(BT_UUID_DRIVE_GUARDIAN_LED_VAL)

/*

SENSOR DATA STRUCT

the device was sending 20 bytes data size instead of 18 bytes over the BLE, which interfered with the data reading on the Android side   
this issue is related to alignment or padding of the struct.

In C, compilers might add padding to structs to ensure proper alignment of data members, especially when dealing with different data types.
This padding can affect the size and layout of the struct in memory.

To ensure proper alignment and matching of data on both the device and Android sides, 
it is necessary to explicitly specify the struct's packing or alignment on the device side. 
This is achieved by using compiler-specific directives or attributes, such as __attribute__((packed)).
*/
typedef struct __attribute__((packed)) {
    uint16_t ppg_led_on;
    int16_t x_axis_data;
    int16_t y_axis_data;
    int16_t z_axis_data;
    int32_t temperature;
    int32_t pressure;
    float soc;
} SensorData;

/** @brief Send the sensor value as notification.
 *
 * @param[in] sensor_value Sensor data struct.
 *
 * @retval 0 If the operation was successful.
 *           Otherwise, a (negative) error code is returned.
 */
int send_sensor_notify(SensorData *data);