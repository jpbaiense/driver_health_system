/*
 * Copyright (c) 2018 Nordic Semiconductor ASA
 *
 * SPDX-License-Identifier: LicenseRef-Nordic-5-Clause
 */

#include "sensor_data.h"

LOG_MODULE_DECLARE(DriveGuardian);

static bool notify_sensordata_enabled;

/* Configuration change callback function for the sensor data characteristic */
static void sensordata_ccc_sensordata_cfg_changed(const struct bt_gatt_attr *attr, uint16_t value)
{
	notify_sensordata_enabled = (value == BT_GATT_CCC_NOTIFY);
}

/* Service Declaration */
BT_GATT_SERVICE_DEFINE(
	drive_guardian, BT_GATT_PRIMARY_SERVICE(BT_UUID_DRIVE_GUARDIAN),
	BT_GATT_CHARACTERISTIC(BT_UUID_DRIVE_GUARDIAN_BUTTON, BT_GATT_CHRC_READ | BT_GATT_CHRC_INDICATE,
			       BT_GATT_PERM_READ, NULL, NULL, NULL),
	
	BT_GATT_CCC(NULL, BT_GATT_PERM_READ | BT_GATT_PERM_WRITE),

	BT_GATT_CHARACTERISTIC(BT_UUID_DRIVE_GUARDIAN_LED, BT_GATT_CHRC_WRITE, BT_GATT_PERM_WRITE, NULL,
			       NULL, NULL),
	/* Create and add the MYSENSOR characteristic and its CCCD  */
	BT_GATT_CHARACTERISTIC(BT_UUID_DRIVE_GUARDIAN_MYSENSOR, BT_GATT_CHRC_NOTIFY, BT_GATT_PERM_NONE, NULL,
			       NULL, NULL),
	/* Create and add the Client Characteristic Configuration Descriptor */
	BT_GATT_CCC(sensordata_ccc_sensordata_cfg_changed, BT_GATT_PERM_READ | BT_GATT_PERM_WRITE)
);

/* Function to send notifications for the sensor data characteristic */
int send_sensor_notify(SensorData *data)
{
	if (!notify_sensordata_enabled) {
		return -EACCES;
	}

	return bt_gatt_notify(NULL, &drive_guardian.attrs[7], data, sizeof(SensorData));
}