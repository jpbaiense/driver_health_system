/*
      BMP581 Sensor Driver
      Author: João Pedro Baiense
      Date: 29/12/2023
      based on: 
      https://github.com/Badger-Embedded/BMP581-Zephyr
      https://github.com/kkingsbe/STM-32-Libraries/tree/dc09c3a8cde5ed55a776936dd62454799e681db0/BMP581
      https://learn.sparkfun.com/tutorials/qwiic-pressure-sensor-bmp581-hookup-guide/all
*/

#include <zephyr/drivers/gpio.h>
#include <zephyr/types.h>
#include <zephyr/sys/util.h>
#include <zephyr/kernel.h>
#include <zephyr/device.h>
#include <zephyr/devicetree.h>
#include <zephyr/drivers/i2c.h> // header file of the I2C API
#include <zephyr/logging/log.h>
#include <math.h>
#include <stdio.h>
#include <stdint.h>


#define BMP5_OK									  0 
#define BMP5_SUCCESS                              0

/* BMP5 Registers */
#define BMP5_REG_CHIP_ID                          0x01 
#define BMP5_REG_TEMP_DATA_XLSB                   0x1D 
#define BMP5_REG_OSR_CONFIG                       0x36 
#define BMP5_REG_ODR_CONFIG                       0x37 
/* endof BMP5 Registers */

/* Chip id of BMP5 */
#define BMP5_CHIP_ID_PRIM                         0x50

#define BMP5_E_NULL_PTR                           -1

#define BMP5_OSR_PRESSURE_ENABLE                  0x20

/* Powermode */
#define BMP5_CONTINUOUS_ODR                        0x03

#define BMP5_SLEEP_ODR                            0

uint8_t bmp581_init();

uint32_t BMP581_get_temp_press_val(int32_t *temperature_degrees, int32_t *pressure_pascal);