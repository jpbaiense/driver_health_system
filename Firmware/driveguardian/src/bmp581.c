#include "bmp581.h"
#define LOG_LEVEL CONFIG_FOUNDRIES_IO_MOD_LOG_LEVEL
#define LOG_MODULE_NAME foundries_io_mod
#include <zephyr/logging/log.h>

LOG_MODULE_DECLARE();

/* Node identifier of the sensor */
#define I2C0_NODE DT_NODELABEL(bmp581)

/* API-specific device structure */
static const struct i2c_dt_spec dev_i2c = I2C_DT_SPEC_GET(I2C0_NODE);

uint8_t bmp581_init(void){

    /*
    
    1 - CHIP ID
    2 - ODR (0x0)
    3- ODR (0x3)


    TEMP XLSB (6 readings to give temp and pressure)
    
    */

    int ret;

    if (!device_is_ready(dev_i2c.bus)) {
		LOG_INF("I2C bus %s is not ready (BMP581 source)!\n\r",dev_i2c.bus->name);
        return BMP5_E_NULL_PTR;
	} else {
        LOG_INF("I2C bus %s for Pressure is ready!\n\r",dev_i2c.bus->name);
    }

    uint8_t chip_id[2] = {BMP5_REG_CHIP_ID,0};
    ret = i2c_write_read_dt(&dev_i2c,&chip_id[0],1,&chip_id[1],1);
    if(ret != 0 && chip_id[1] != BMP5_CHIP_ID_PRIM){
        LOG_INF("Failed to write to I2C device address %x at Reg. %x (BMP581 CHIP ID) \n\r", dev_i2c.addr, chip_id[0]);
        return BMP5_E_NULL_PTR;
    } else {
        LOG_INF("Pressure sensor chip ID is: %x \n\r", chip_id[1]);
    }

    uint8_t osr_press_en[2] = {BMP5_REG_OSR_CONFIG,BMP5_OSR_PRESSURE_ENABLE};
    ret = i2c_write_dt(&dev_i2c,osr_press_en,sizeof(osr_press_en));
    if(ret != 0){
        LOG_INF("Failed to write to I2C device address %x at Reg. %x (BMP581 OSR PRESSURE ENABLE) \n\r", dev_i2c.addr, osr_press_en[0]);
        return BMP5_E_NULL_PTR;
    } else {
        LOG_INF("BMP581 OSR PRESSURE ENABLE fully set!\n\r");
    }

    uint8_t odr_sleep[2] = {BMP5_REG_ODR_CONFIG,BMP5_SLEEP_ODR};
    ret = i2c_write_dt(&dev_i2c,odr_sleep,sizeof(odr_sleep));
    if(ret != 0){
        LOG_INF("Failed to write to I2C device address %x at Reg. %x (BMP581 SLEEP ODR) \n\r", dev_i2c.addr, odr_sleep[0]);
        return BMP5_E_NULL_PTR;
    } else {
        LOG_INF("BMP581 ODR Sleep fully set!\n\r");
    }

    uint8_t odr_continuous[2] = {BMP5_REG_ODR_CONFIG,BMP5_CONTINUOUS_ODR};
    ret = i2c_write_dt(&dev_i2c,odr_continuous,sizeof(odr_continuous));
    if(ret != 0){
        LOG_INF("Failed to write to I2C device address %x at Reg. %x (BMP581 Continuous ODR) \n\r", dev_i2c.addr, odr_continuous[0]);
        return BMP5_E_NULL_PTR;
    } else {
        LOG_INF("BMP581 Continuous ODR fully set!\n\r");
    }

    return BMP5_SUCCESS;
}

/*****************************************************************************
DATA ACQUISITION FUNCTIONS
******************************************************************************/

uint32_t BMP581_get_temp_press_val(int32_t *temperature_degrees, int32_t *pressure_pascal)
{
	int ret = 0;

    uint8_t temp_press_data[6] = {0,0,0,0,0,0};
    uint8_t val_reg = BMP5_REG_TEMP_DATA_XLSB;

	ret = i2c_write_read_dt(&dev_i2c,&val_reg, 1, temp_press_data, 6);
	if(ret != 0){
        LOG_INF("Failed to write to I2C device address %x (BMP581) at Reg. %x \n\r", dev_i2c.addr, BMP5_REG_TEMP_DATA_XLSB);
		return BMP5_E_NULL_PTR;		//error check
	}
    
	if (ret == BMP5_OK)
	{
		/* Perform a division by 2^16 (according to the datasheet) for the temperature measured values to get the temperature data in deg C */
		int32_t raw_temperature = (int32_t)((uint32_t)(temp_press_data[2] << 16) | (uint16_t)(temp_press_data[1] << 8) | temp_press_data[0]);
        *temperature_degrees = (int32_t)(raw_temperature / 65536.0);

        /* Perform a division by 2^6 (according to the datasheet) for the pressured measured values to get the pressure data in Pa */
        uint32_t raw_pressure = (uint32_t)((uint32_t)(temp_press_data[5] << 16) | (uint16_t)(temp_press_data[4] << 8) | temp_press_data[3]);
        *pressure_pascal = (int32_t)(raw_pressure / 64.0);
		
        //LOG_INF("temperature value: %f and pressure value %f \n\r", *temperature_degrees, *pressure_pascal);

	}

	return ret;
}