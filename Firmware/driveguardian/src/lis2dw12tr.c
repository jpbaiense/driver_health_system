#include "lis2dw12tr.h"

#define LOG_LEVEL CONFIG_FOUNDRIES_IO_MOD_LOG_LEVEL
#define LOG_MODULE_NAME foundries_io_mod
#include <zephyr/logging/log.h>

LOG_MODULE_DECLARE();

/* Node identifier of the sensor */
#define I2C0_NODE DT_NODELABEL(lis2dw12)

/* API-specific device structure */
static const struct i2c_dt_spec dev_i2c = I2C_DT_SPEC_GET(I2C0_NODE);

uint8_t lis2dw12_init(void)
{
    /* Order to initialize the sensor
    1- WHO AM I R
    2- CTRL1 R/W -> HIGH_PERFORMANCE_MODE, LP_MODE2, LOW_POWER_MODE_12_5HZ
    3- CTRL2 R/W -> IF_ADD_INC_MSK, BDU_MSK, SOFT_RESET
    6- CTRL6 W -> FS_2G, LOW_PASS_FILTER, BW_FILT_ODR/4

    7- READ DATA
    
    To turn on the accelerometer and gather acceleration data, it is necessary to select one of the operating modes 
    through the CTRL1 register <- LIS2DW12 User Manual
    
    */

   	int ret;

	/* With this, we have the pointer to the device structure of the I2C controller and
	the sensor address(slave device address) and 
	can start using the I2C driver API to configure the sensor connected to the I2C controller.*/
    if (!device_is_ready(dev_i2c.bus)) {
		LOG_INF("I2C bus %s is not ready (LIS2DW12TR source)!\n\r",dev_i2c.bus->name);
		return -1;
	} else {
        LOG_INF("I2C bus %s for IMU is ready!\n\r",dev_i2c.bus->name);
    }

    /* GET WHO AM I -> DEVICE ID */
    uint8_t who_am_i[2]={WHO_AM_I, 0};
    ret = i2c_write_read_dt(&dev_i2c,&who_am_i[0],1,&who_am_i[1],1); // maybe use this i2c_write_read
    if(ret != 0 && who_am_i[1] != 0x44){
        LOG_INF("Failed to write to I2C device address %x (LIS2DW12TR WHO AM I) at Reg. %x \n\r", dev_i2c.addr, who_am_i[0]);
        return -1;
    } else {
        LOG_INF("IMU Device ID is: %x \n\r", who_am_i[1]);
    }

    /* CTRL2 configuration

    - (BDU) Block data update -> 1: output registers not updated until MSB and LSB read
    - (SOFT_RESET) Soft reset acts as reset for all control registers, then goes to 0 -> 1: enabled
    - (IF_ADD_INC) Register address automatically incremented during multiple byte access with a serial interface I2C -> 1: enabled
    */
    
    uint8_t ctrl2_config[2] = {CTRL2, BDU_MSK | SOFT_RESET_MSK | IF_ADD_INC_MSK}; // TODO: Check if this bitwise operation is correct
    ret = i2c_write_dt(&dev_i2c, ctrl2_config, sizeof(ctrl2_config));
    if(ret != 0){
		LOG_INF("Failed to write to I2C device address %x (LIS2DW12TR CTRL 2) at Reg. %x \n", dev_i2c.addr,ctrl2_config[0]);
        return -1;
	} else {
        LOG_INF("LIS2DW12TR Control 2 successfully written!\n\r");
    }

    /* CTRL1 configuration

    - (LP_MODE) Low-power mode selection ->  Low-Power Mode 2 (14-bit resolution)
    - (MODE) Mode selection -> High-Performance Mode (14-bit resolution)
    - (ODR) Output data rate and mode selection -> High-Performance / Low-Power mode 12.5 Hz
    */

    uint8_t ctrl1_config[2] = {CTRL1, LP_MODE2 | HIGH_PERFORMANCE_MODE | LOW_POWER_MODE_12_5HZ}; // TODO: Check if this bitwise operation is correct
    ret = i2c_write_dt(&dev_i2c, ctrl1_config, sizeof(ctrl1_config)); 
    if(ret != 0){
		LOG_INF("Failed to write to I2C device address %x (LIS2DW12TR CTRL 1) at Reg. %x \n", dev_i2c.addr,ctrl1_config[0]);
        return -1;
	} else {
        LOG_INF("LIS2DW12TR Control 1 successfully written!\n\r");
    }

    /* CTRL6 configuration

    - (FS) Full-scale selection ->   ±2 g
    - (FDS) Filtered data type selection -> 0: low-pass filter path selected
    - (BW_FILT)  Bandwidth selection -> ODR/4 (HP/LP)
    */

    uint8_t ctrl6_config[2] = {CTRL6, FS_2G | FDS_POS | BW_FILT_ODR_4}; // TODO: Check if this bitwise operation is correct
    ret = i2c_write_dt(&dev_i2c, ctrl6_config, sizeof(ctrl6_config));
    if(ret != 0){
		LOG_INF("Failed to write to I2C device address %x (LIS2DW12TR CTRL 6) at Reg. %x \n", dev_i2c.addr,ctrl6_config[0]);
        return -1;
	} else {
        LOG_INF("LIS2DW12TR Control 6 successfully written!\n\r");
    }

    return 0;
}


/*          LIS2DW12 User Manual
    The read operations should be performed as follows:
    1. Read the STATUS register.
    2. If DRDY = 0, then go to 1.
    3. Read OUT_X_L.
    4. Read OUT_X_H.
    5. Read OUT_Y_L.
    6. Read OUT_Y_H.
    7. Read OUT_Z_L.
    8. Read OUT_Z_H.
    9. Data processing
    10. Go to 1.


    The measured acceleration data are sent to the OUT_X_H, OUT_X_L, OUT_Y_H, OUT_Y_L, OUT_Z_H, and 
    OUT_Z_L registers. The complete output data for the X, Y, Z channels is given by the concatenation OUT_X_H & OUT_X_L, 
    OUT_Y_H & OUT_Y_L, OUT_Z_H & OUT_Z_L.
    
    After calculating the LSB, it must be multiplied by the proper sensitivity parameter to obtain the corresponding value in mg. 

    Example of output data:

    - Get raw data from the sensor (high-performance mode, ±2 g) -> OUT_X_L: 60h and OUT_X_H: FDh
    - Do register concatenation -> OUT_X_H & OUT_X_L: FD60h
    - Apply sensitivity (for example, 14-bit resolution, 0.244 at full scale ±2 g) -> X: -672 / 4 * 0.244 = -41 mg
*/

uint32_t get_x_axes_data(void){
    int ret;
    uint8_t x_axes_raw_data[2] = {0, 0};
    uint8_t x_axes_regs[2] = {OUT_X_L, OUT_X_H};
    ret = i2c_write_read_dt(&dev_i2c,&x_axes_regs[0],1,&x_axes_raw_data[0],1);
    if(ret != 0){
		LOG_INF("Failed to write/read I2C device address %x (LIS2DW12TR) at Reg. %x \r\n", dev_i2c.addr,x_axes_regs[0]);
	}
	ret = i2c_write_read_dt(&dev_i2c,&x_axes_regs[1],1,&x_axes_raw_data[1],1);
	if(ret != 0){
		LOG_INF("Failed to write/read I2C device address %x (LIS2DW12TR) at Reg. %x \r\n", dev_i2c.addr,x_axes_regs[1]);
	}

    int16_t x_axes_calculation = (((int16_t) x_axes_raw_data[1]) << 8) | x_axes_raw_data[0]; // shift the MSB register to form a 16 bit word that integrates X's low and high bits 
    int16_t x_axes_twos_complement;

    if (x_axes_calculation & 0x8000) { // this checks if the number is negative or positive by applying AND bit-by-bit, if the MSB is 1 then it's negative and needs to increment by 1
        // The value is negative
       x_axes_twos_complement = ((~x_axes_calculation) + 0x1) * -1;
    } else { // it's a positive value, no need to change anything
        x_axes_twos_complement = x_axes_calculation;
    }

    int16_t x_axes_converted_data = (x_axes_twos_complement/4) * 0.244; // formula to convert to mg for 14-bit resolution, 0.244 at full scale ±2 g based on the User Manual
    return x_axes_converted_data; // returns the value, but without the minus signal when it's negative - TODO: I think I fixed it, test it
}

uint32_t get_y_axes_data(void){
    int ret;
    uint8_t y_axes_raw_data[2] = {0, 0};
    uint8_t y_axes_regs[2] = {OUT_Y_L, OUT_Y_H};
    ret = i2c_write_read_dt(&dev_i2c,&y_axes_regs[0],1,&y_axes_raw_data[0],1);
    if(ret != 0){
		LOG_INF("Failed to write/read I2C device address %x (LIS2DW12TR) at Reg. %x \r\n", dev_i2c.addr,y_axes_regs[0]);
	}
	ret = i2c_write_read_dt(&dev_i2c,&y_axes_regs[1],1,&y_axes_raw_data[1],1);
	if(ret != 0){
		LOG_INF("Failed to write/read I2C device address %x (LIS2DW12TR) at Reg. %x \r\n", dev_i2c.addr,y_axes_regs[1]);
	}
    
    int16_t y_axes_calculation = (((int16_t) y_axes_raw_data[1]) << 8) | y_axes_raw_data[0];
    int16_t y_axes_twos_complement;

    if (y_axes_calculation & 0x8000) {
       y_axes_twos_complement = ((~y_axes_calculation) + 0x1) * -1;
    } else {
        y_axes_twos_complement = y_axes_calculation;
    }

    int16_t y_axes_converted_data = (y_axes_twos_complement/4) * 0.244;
    return y_axes_converted_data;
}

uint32_t get_z_axes_data(void){
    int ret;
    uint8_t z_axes_raw_data[2] = {0, 0};
    uint8_t z_axes_regs[2] = {OUT_Z_L, OUT_Z_H};
    ret = i2c_write_read_dt(&dev_i2c,&z_axes_regs[0],1,&z_axes_raw_data[0],1);
    if(ret != 0){
		LOG_INF("Failed to write/read I2C device address %x (LIS2DW12TR) at Reg. %x \r\n", dev_i2c.addr,z_axes_regs[0]);
	}
	ret = i2c_write_read_dt(&dev_i2c,&z_axes_regs[1],1,&z_axes_raw_data[1],1);
	if(ret != 0){
		LOG_INF("Failed to write/read I2C device address %x (LIS2DW12TR) at Reg. %x \r\n", dev_i2c.addr,z_axes_regs[1]);
	}

    int16_t z_axes_calculation = (((int16_t) z_axes_raw_data[1]) << 8) | z_axes_raw_data[0];
    int16_t z_axes_twos_complement;

    if (z_axes_calculation & 0x8000) {
       z_axes_twos_complement = ((~z_axes_calculation) + 0x1) * -1;
    } else {
        z_axes_twos_complement = z_axes_calculation;
    }

    int16_t z_axes_converted_data = (z_axes_twos_complement/4) * 0.244;
    return z_axes_converted_data; 
}