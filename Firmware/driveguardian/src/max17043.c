#include "max17043.h"
#include <stdbool.h>
#include <stdint.h>

#define LOG_LEVEL CONFIG_FOUNDRIES_IO_MOD_LOG_LEVEL
#define LOG_MODULE_NAME foundries_io_mod
#include <zephyr/logging/log.h>

LOG_MODULE_DECLARE();

/* Node identifier of the sensor */
#define I2C0_NODE DT_NODELABEL(max17043)

/* API-specific device structure */
static const struct i2c_dt_spec dev_i2c = I2C_DT_SPEC_GET(I2C0_NODE);

uint8_t max17043_init(void) 
{
    int ret = 0;

	/* With this, we have the pointer to the device structure of the I2C controller and
	the sensor address(slave device address) and 
	can start using the I2C driver API to configure the sensor connected to the I2C controller.*/
    if (!device_is_ready(dev_i2c.bus)) {
		LOG_INF("I2C bus %s is not ready (MAX17043 source)!\n\r",dev_i2c.bus->name);
		return -1;
	} else {
        LOG_INF("I2C bus %s for Fuel Gauge is ready!\n\r",dev_i2c.bus->name);
    }

    uint8_t version_register= VERSION_REGISTER;
    uint8_t version_raw_value[2]={0, 0};

    ret = i2c_write_read_dt(&dev_i2c,&version_register,1,version_raw_value,2); // maybe use this i2c_write_read
    if(ret != 0){
        LOG_INF("Failed to write to I2C device address %x (MAX17043 VERSION) at Reg. %x \n\r", dev_i2c.addr, version_register);
        return -1;
    } 

    uint16_t version_data = ((version_raw_value[0] << 8) | version_raw_value[1]); // ( (MSB << 8) | LSB )

    LOG_INF("MAX17043 VERSION is: %x \n\r", version_data);

    uint8_t reset_device[3] = {COMMAND_REGISTER, 0x00, 0x54};
    ret = i2c_write_dt(&dev_i2c,reset_device,sizeof(reset_device));
    if(ret != 0){
        LOG_INF("Failed to write to I2C device address %x at Reg. %x (MAX17043 RESET) \n\r", dev_i2c.addr, reset_device[0]);
        return -1;
    } else {
        LOG_INF("MAX17043 RESET fully set!\n\r");
    }

    k_sleep(K_MSEC(250));

    uint8_t quick_start[3] = {MODE_REGISTER, 0x40, 0x00};
    ret = i2c_write_dt(&dev_i2c,quick_start,sizeof(quick_start));
    if(ret != 0){
        LOG_INF("Failed to write to I2C device address %x at Reg. %x (MAX17043 QUICK START) \n\r", dev_i2c.addr, quick_start[0]);
        return -1;
    } else {
        LOG_INF("MAX17043 QUICK START fully set!\n\r");
    }

    k_sleep(K_MSEC(250));

    return ret;
}

uint8_t max17043_get_vcell(float *vcell_data) 
{
    int ret = 0;
	uint8_t vcell_raw_data[2] = {0,0};
    uint8_t vcell_reg = VCELL_REGISTER;

	ret = i2c_write_read_dt(&dev_i2c,&vcell_reg, 1, vcell_raw_data, 2);
	if(ret != 0){
        LOG_INF("Failed to write to I2C device address %x (MAX17043 VCELL MEASUREMENT) at Reg. %x \n\r", dev_i2c.addr, VCELL_REGISTER);
		return -1;		//error check
	}

    uint16_t vcell_shifted = ((vcell_raw_data[0] << 8) | (vcell_raw_data[1]));

    int vcell_converted = (vcell_shifted >> 4);
    
	*vcell_data = (float) vcell_converted * (1.25f / 1000.0f);

    return ret;
}

uint8_t max17043_get_soc(float *soc_val) 
{
    int ret = 0;
	uint8_t soc_raw_data[2] = {0,0};
    uint8_t soc_reg = SOC_REGISTER;

	ret = i2c_write_read_dt(&dev_i2c,&soc_reg, 1, soc_raw_data, 2);
	if(ret != 0){
        LOG_INF("Failed to write to I2C device address %x (MAX17043 SOC MEASUREMENT) at Reg. %x \n\r", dev_i2c.addr, SOC_REGISTER);
		return -1;		//error check
	}

    float percentage = soc_raw_data[0]; // The high byte is the percentage.

    float fraction = soc_raw_data[1] / 256.0f; // low byte contains additional resolution of 1/256%.

    *soc_val = (float) percentage + fraction;

    return ret;	
}