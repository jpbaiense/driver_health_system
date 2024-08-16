/*
      MAX17043 Sensor Driver
      Author: João Pedro Baiense
      Date: 29/12/2023
      based on: 
      https://learn.sparkfun.com/tutorials/lipo-fuel-gauge-max1704x-hookup-guide/all#breakout-board-max17043-hardware-overview
      https://github.com/koson/Altium-Project/blob/c4c576b9b974947f92309de567380dff323ca40e/nrf52-quadcopter/Firmware/drivers/max17043.h
      https://cdn.sparkfun.com/datasheets/Prototyping/MAX17043-MAX17044.pdf
*/

#ifndef _MAX17043_H
#define _MAX17043_H

#include <zephyr/kernel.h>
#include <zephyr/device.h>
#include <zephyr/devicetree.h>
#include <zephyr/drivers/i2c.h> // header file of the I2C API
#include <zephyr/logging/log.h>
#include <math.h>
#include <stdio.h>
#include <stdint.h>

//Device address
#define MAX17043_ADDRESS		0x36

//Internal registers
#define VCELL_REGISTER			0x02
#define SOC_REGISTER				0x04
#define MODE_REGISTER				0x06
#define VERSION_REGISTER		0x08
#define CONFIG_REGISTER			0x0C
#define COMMAND_REGISTER		0xFE

uint8_t max17043_init(void);

uint8_t max17043_get_vcell(float *vcell_data);
uint8_t max17043_get_soc(float *soc_val);
#endif