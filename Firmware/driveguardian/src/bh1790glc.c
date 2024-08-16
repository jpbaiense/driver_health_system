/*
Measurement Sequence
1. Power On
2. Start measurement by writing the parameters of address 41h (MEAS_CONTROL1), 42h (MEAS_CONTROL2) and 43h (MEAS_START).
3. 
- Read data in order of address 54h and 55h (DATAOUT_LEDOFF) to 56h and 57 (DATAOUT_LEDON) after measurement complementation.
- Measurement finished within measurement time(Note 1 - Measurement time is changed by “RCYCLE” (Address 41h))
- Reading the data of address 57h (DATAOUT_LEDON) restart measurement.
4. After restart of measurement, repeat step3 then measurement data can be read.
5. Write “SWRESET=1” (Address 40h), when stop measurement or change parameter.
Repeat from step 2, when start measurement again.
6. Parameter refreshment is recommended. Write parameter of address 41h to 43h regularly after reading data
*/

#include "bh1790glc.h"

#define LOG_LEVEL CONFIG_FOUNDRIES_IO_MOD_LOG_LEVEL
#define LOG_MODULE_NAME foundries_io_mod
#include <zephyr/logging/log.h>

/*
 * Note LOG_MODULE_DECLARE() is used instead of LOG_MODULE_REGISTER().
 * Exactly one file registers the module. The other files in the
 * module still need to declare module-specific state before using
 * logger APIs.
 */
LOG_MODULE_DECLARE();

/* Get the node identifier of the sensor */
#define I2C0_NODE DT_NODELABEL(bh1790)

/* Retrieve the API-specific device structure and make sure that the device is ready to use  */
static const struct i2c_dt_spec dev_i2c = I2C_DT_SPEC_GET(I2C0_NODE);

#define NUM_SAMPLES			500
uint16_t ppg_data[2];

/*****************************************************************************
INIT FUNCTION
******************************************************************************/

uint8_t BH1790GLC_init(void){
    int ret;

    if (!device_is_ready(dev_i2c.bus)) {
		LOG_INF("I2C bus %s is not ready (BH1790 source)!\n\r",dev_i2c.bus->name);
        return ERR_DEVICE_NOT_READY;
	} else {
        LOG_INF("I2C bus %s for PPG sensor is ready!\n\r",dev_i2c.bus->name);
    }

    uint8_t manuf_id[2] = {BH1790GLC_MANUFACTURER_ID,0};
    ret = i2c_write_read_dt(&dev_i2c,&manuf_id[0],1,&manuf_id[1],1);
    if(ret != 0 && manuf_id[1] != 0xE0){
        LOG_INF("Failed to write to I2C device address %x at Reg. %x (PPG Manufacturer ID) \n\r", dev_i2c.addr, manuf_id[0]);
        return ERR_MID_VAL;
    } else {
        LOG_INF("PPG Manufacturer ID is: %x \n\r", manuf_id[1]);
    }

    uint8_t part_id[2] = {BH1790GLC_PART_ID,0};
    ret = i2c_write_read_dt(&dev_i2c,&part_id[0],1,&part_id[1],1);
    if(ret != 0 && part_id[1] != 0x0D){
        LOG_INF("Failed to write to I2C device address %x at Reg. %x (PPG Part ID) \n\r", dev_i2c.addr, part_id[0]);
        return ERR_PID_VAL;
    } else {
        LOG_INF("PPG Part ID is: %x \n\r", part_id[1]);
    }

    uint8_t meas_ctrl_1[2] = {BH1790GLC_MEAS_CONTROL1,BH1790GLC_MEAS_CONTROL1_VAL};
    ret = i2c_write_dt(&dev_i2c,meas_ctrl_1,sizeof(meas_ctrl_1));
    if(ret != 0){
        LOG_INF("Failed to write to I2C device address %x at Reg. %x (PPG Measurement control 1) \n\r", dev_i2c.addr, meas_ctrl_1[0]);
        return ERR_MEAS_CONTROL1;
    } else {
        LOG_INF("PPG Measurement control 1 fully set!\n\r");
    }

    uint8_t meas_ctrl_2[2] = {BH1790GLC_MEAS_CONTROL2,BH1790GLC_MEAS_CONTROL2_VAL};
    ret = i2c_write_dt(&dev_i2c,meas_ctrl_2,sizeof(meas_ctrl_2));
    if(ret != 0){
        LOG_INF("Failed to write to I2C device address %x at Reg. %x (PPG Measurement control 2) \n\r", dev_i2c.addr, meas_ctrl_2[0]);
        return ERR_MEAS_CONTROL2;
    } else {
        LOG_INF("PPG Measurement control 2 fully set!\n\r");
    }

    uint8_t meas_start[2] = {BH1790GLC_MEAS_START,BH1790GLC_MEAS_START_VAL};
    ret = i2c_write_dt(&dev_i2c,meas_start,sizeof(meas_start));
    if(ret != 0){
        LOG_INF("Failed to write to I2C device address %x at Reg. %x (PPG Start) \n\r", dev_i2c.addr, meas_start[0]);
        return ERR_MEAS_START;
    } else {
        LOG_INF("PPG Start fully set!\n\r");
    }

    return SUCCESS;
}

/*****************************************************************************
DATA ACQUISITION FUNCTIONS
******************************************************************************/
/*
 * Retrieves the ppg readings and stores them into *ppg_led_off and *ppg_led_on
 */
uint8_t BH1790GLC_get_val(uint16_t *ppg_led_off, uint16_t *ppg_led_on)
{
	int ret;
	uint8_t sensorData[4] = {0,0,0,0};
    uint8_t val_reg = BH1790GLC_DATAOUT_LEDOFF_L;

	ret = i2c_write_read_dt(&dev_i2c,&val_reg, 1, sensorData, 4);
	if(ret != 0){
        LOG_INF("Failed to write to I2C device address %x (PPG Data ON and OFF Measurement) at Reg. %x \n\r", dev_i2c.addr, BH1790GLC_DATAOUT_LEDOFF_L);
		return ERR_DATA_OUT;		//error check
	}

	//convert the sensorData values to useful data
	*ppg_led_off = ((uint16_t)sensorData[1]<<8)|(sensorData[0]);	//LED OFF
	*ppg_led_on = ((uint16_t)sensorData[3]<<8)|(sensorData[2]);	//LED ON

    //LOG_INF("PPG value retrieved: %d and %d \n\r", *ppg_led_off, *ppg_led_on);

	return SUCCESS;
}

/*
 * Writes “SWRESET=1” (Address 40h), when stop measurement or change parameter.
 * This will wipe all registers
 */
uint8_t BH1790GLC_reset_device(){
	int ret;
	uint8_t reset_config[2] = {BH1790GLC_RESET,BH1790GLC_SWRESET};

	ret = i2c_write_dt(&dev_i2c, reset_config, sizeof(reset_config));
	if(ret != 0){
		return ERR_RESET;
	}

	return SUCCESS;
}

/*
 * Run this periodically (according to the datasheet) after reading data
 */

uint8_t BH1790GLC_param_refreshment(){
	/* Keep count of errors */
	int ret;

	/* Configure the 3 registers needed to start taking measurements */
    uint8_t meas_ctrl_1[2] = {BH1790GLC_MEAS_CONTROL1,BH1790GLC_MEAS_CONTROL1_VAL};
    ret = i2c_write_dt(&dev_i2c,meas_ctrl_1,sizeof(meas_ctrl_1)); 
    if(ret != 0){
        return ERR_MEAS_CONTROL1;
    }

    uint8_t meas_ctrl_2[2] = {BH1790GLC_MEAS_CONTROL2,BH1790GLC_MEAS_CONTROL2_VAL};
    ret = i2c_write_dt(&dev_i2c,meas_ctrl_2,sizeof(meas_ctrl_2)); 
    if(ret != 0){
        return ERR_MEAS_CONTROL2;
    }

    uint8_t meas_start[2] = {BH1790GLC_MEAS_START,BH1790GLC_MEAS_START_VAL};
    ret = i2c_write_dt(&dev_i2c,meas_start,sizeof(meas_start)); 
    if(ret != 0){
        return ERR_MEAS_START;
    }

	return SUCCESS;
}