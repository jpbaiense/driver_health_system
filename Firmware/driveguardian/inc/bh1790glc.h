/*
      BH1790GLC Sensor Driver
      Author: João Pedro Baiense
      Date: 29/12/2023
      based on: 
      https://github.com/looseleif/cardiac-wearable/blob/9faa5559f500fb312713b37a420e76c9843c2194/cardiac-wearable-firmware/AWEAR_proj/Core/Inc/BH1790GLC.h
      https://www.youtube.com/watch?v=prZomlfA_bI
      https://www.rohm.com/sensor-shield-support/heart-rate-sensor
      https://github.com/ROHMUSDC/BH1790GLC-EVK-001?tab=readme-ov-file
      https://www.canariatechnologies.com/post/what-is-ppg-technology-and-how-does-it-work
      https://soulfit.io/blog/how-does-ppg-technology-works/
*/

/* HEADER FILES */

#include <zephyr/kernel.h>
#include <zephyr/device.h>
#include <zephyr/devicetree.h>
#include <zephyr/drivers/i2c.h>
#include <zephyr/logging/log.h>
#include <math.h>
#include <stdio.h>
#include <stdint.h>

/* REGISTER MAP DEFINITIONS */

#define BH1790GLC_MANUFACTURER_ID     0x0F // Manufacturer ID : 0xE0 -> address 0x92 can also be used
#define BH1790GLC_PART_ID     0x10 // part ID : 0x0D
#define BH1790GLC_RESET     0x40 //  SWRESET
#define BH1790GLC_MEAS_CONTROL1     0x41 // Measurement setting Control
#define BH1790GLC_MEAS_CONTROL2     0x42 // Measurement setting Control
#define BH1790GLC_MEAS_START     0x43 // Start Measurement
#define BH1790GLC_DATAOUT_LEDOFF_L     0x54 // Measurement Data (LED OFF) 
#define BH1790GLC_DATAOUT_LEDOFF_H     0x55 // Measurement Data (LED OFF)
#define BH1790GLC_DATAOUT_LEDON_L     0x56 // Measurement Data (LED ON)
#define BH1790GLC_DATAOUT_LEDON_H     0x57 // Measurement Data (LED ON)

/********************Bits Definition*********************/

/*
  @brief: RESET
  @default: 00000000
  */

#define BH1790GLC_SWRESET (1 << 7) // Reset process is performed when writing SWRESET=1.

/*
  @brief: MEAS_CONTROL1
  @default: 00000000
  */

/* READY 
OSC block is active at “RDY=1”.
OSC block is supply clock to internal block.
*/
#define BH1790GLC_MEAS_CONTROL1_RDY                     (1 << 7)

/* Select LED emitting frequency */
#define BH1790GLC_MEAS_CONTROL1_LED_LIGHTING_FREQ_128HZ (0 << 2) // 128 Hz mode
#define BH1790GLC_MEAS_CONTROL1_LED_LIGHTING_FREQ_64HZ (1 << 2) // 64 Hz mode

/* RECYCLE 
Select Measurement time corresponding to data reading frequency. Measurement time depends on OSC cycle
64Hz Mode : 7370 x tosc ms
32Hz Mode : 14740 x tosc ms
*/
#define BH1790GLC_MEAS_CONTROL1_RCYCLE_64HZ             (1 << 0) // 64Hz Mode
#define BH1790GLC_MEAS_CONTROL1_RCYCLE_32HZ             (2 << 0) // 32Hz Mode

/*
  @brief: MEAS_CONTROL2
  @default: 00000000
  */

/* LED_EN
Select LED driver mode

Pulsed light emit after starting measurement(Write “MEAS_ST=1” or Read address 57h).
No light emit after measurement completion
*/
#define BH1790GLC_MEAS_CONTROL2_LED_EN_00               (0 << 6)
#define BH1790GLC_MEAS_CONTROL2_LED_EN_LED1_CONSTANT               (1 << 6) // LED 1 Constant Light Emission
#define BH1790GLC_MEAS_CONTROL2_LED_EN_LED2_CONSTANT               (2 << 6) // LED 2 Constant Light Emission
#define BH1790GLC_MEAS_CONTROL2_LED_EN_LED1_LED2_CONSTANT               (3 << 6) // LED 1 and LED 2 Constant Light Emission

/* LED_ON_TIME 
Select LED emitting time.
LED emitting time depends on by OSC cycle.
0.3ms Mode : 216 x tosc μs
0.6ms Mode : 432 x tosc μs
*/
#define BH1790GLC_MEAS_CONTROL2_LED_ON_TIME_0_3MS       (0 << 5) //  0.3ms Mode
#define BH1790GLC_MEAS_CONTROL2_LED_ON_TIME_0_6MS       (1 << 5) //  0.6ms Mode

/* LED_CURRENT
Select LED lighting current
*/
#define BH1790GLC_MEAS_CONTROL2_LED_CURRENT_0MA        (0 << 0) // 10mA Mode
#define BH1790GLC_MEAS_CONTROL2_LED_CURRENT_1MA        (0x8 << 0) // 1mA Mode
#define BH1790GLC_MEAS_CONTROL2_LED_CURRENT_2MA        (0x9 << 0) // 2mA Mode
#define BH1790GLC_MEAS_CONTROL2_LED_CURRENT_3MA        (0xA << 0) // 3mA Mode
#define BH1790GLC_MEAS_CONTROL2_LED_CURRENT_6MA        (0xB << 0) // 6mA Mode
#define BH1790GLC_MEAS_CONTROL2_LED_CURRENT_10MA        (0xC << 0) // 10mA Mode
#define BH1790GLC_MEAS_CONTROL2_LED_CURRENT_20MA        (0xD << 0) // 20mA Mode
#define BH1790GLC_MEAS_CONTROL2_LED_CURRENT_30MA        (0xE << 0) // 30mA Mode
#define BH1790GLC_MEAS_CONTROL2_LED_CURRENT_60MA        (0xF << 0) // 60mA Mode

/*
  @brief: MEAS_CONTROL2
  @default: 00000000
  */
 /* MEAS_ST
Start measurement by writing “MEAS_ST=1” after writing “RDY=1”.
Measurement doesn’t restart if writing “MEAS_ST=1” after start measurement.
When stop measurement, write “SWRESET=1” without writing “MEAS_ST=0”
*/
#define BH1790GLC_MEAS_START_MEAS_ST                    (1 << 0)

/* PRE-SETS*/
#define BH1790GLC_MEAS_CONTROL1_VAL   (BH1790GLC_MEAS_CONTROL1_RDY | BH1790GLC_MEAS_CONTROL1_LED_LIGHTING_FREQ_128HZ | BH1790GLC_MEAS_CONTROL1_RCYCLE_32HZ)
#define BH1790GLC_MEAS_CONTROL2_VAL   (BH1790GLC_MEAS_CONTROL2_LED_EN_00 | BH1790GLC_MEAS_CONTROL2_LED_ON_TIME_0_3MS | BH1790GLC_MEAS_CONTROL2_LED_CURRENT_10MA)
#define BH1790GLC_MEAS_START_VAL      (BH1790GLC_MEAS_START_MEAS_ST)

/*****************************************************************************
ERROR HANDLING
******************************************************************************/
#define SUCCESS				0

#define ERR_MID_VAL			10
#define ERR_PID_VAL			11

#define ERR_MEAS_CONTROL1	20
#define ERR_MEAS_CONTROL2	21
#define ERR_MEAS_START		22

#define ERR_DATA_OUT		30

#define ERR_RESET			90

#define ERR_DEVICE_NOT_READY 40

/*****************************************************************************
INIT FUNCTION
******************************************************************************/
uint8_t BH1790GLC_init();

/*****************************************************************************
DATA ACQUISITION FUNCTIONS
******************************************************************************/
uint8_t BH1790GLC_get_val(uint16_t *ppg_led_off, uint16_t *ppg_led_on);
uint8_t BH1790GLC_reset_device();
uint8_t BH1790GLC_param_refreshment();