/* 
        I2C Example using NRF Connect SDK
    https://academy.nordicsemi.com/courses/nrf-connect-sdk-fundamentals/lessons/lesson-6-serial-com-i2c/topic/exercise-1-6-2/
*/

// include the header file of the I2C API
#include <zephyr/drivers/i2c.h>
#include <zephyr/drivers/sensor.h>

// To display the sensor readings on the console, we will use the simple printk()
// Include the header file <sys/printk.h> to use printk()
#include <zephyr/sys/printk.h>

#include "LIS2DW12.h"

// Get the node identifier of the sensor
#define I2C0_NODE DT_NODELABEL(lis2dw12tr)

/*
    Define the addresses of the relevant registers (from the sensor datasheet). 
    Typically this information goes into a separate header file (.h). 
    However, for the sake of keeping this demonstration simple, we will add them in main.c.

    I AM USING THE STTS751 SENSOR JUST FOR DEMONSTRATION BECAUSE OF THE TUTORIAL
*/



/*
    Retrieve the API-specific device structure and make sure that the device is ready to use
    With this, we have the pointer to the device structure of the I2C controller and the sensor address(slave device address) 
    and can start using the I2C driver API to configure the sensor connected to the I2C controller
*/

int i2c_init(void){
    static const struct i2c_dt_spec dev_lis2dw12 = I2C_DT_SPEC_GET(I2C0_NODE);

    if (!device_is_ready(dev_lis2dw12.bus)) {
        printk("I2C bus %s is not ready!\n\r",dev_lis2dw12.bus->name);
        return 1;
    }

    int ret = 0;
    uint8_t who_am_i_read = 0;
    ret = i2c_burst_read_dt(&dev_lis2dw12, WHO_AM_I, who_am_i_read, sizeof(who_am_i_read));
    if(ret != 0){
		printk("Failed to read to I2C device address 0x%c at Reg. 0x%c\n", dev_lis2dw12.addr, WHO_AM_I);
        return 1;
	}

    if(who_am_i_read != WHO_AM_I_VALUE){
        printk("Wrong WHO_AM_I value: %c\n", who_am_i_read);
        return 1;
    }

    // CTRL 1
    uint8_t config_ctrl_1[2] = {CTRL1,LOW_POWER_MODE_12_5HZ || HIGH_PERFORMANCE_MODE || LP_MODE4};
    ret = i2c_write_dt(&dev_lis2dw12, config_ctrl_1, sizeof(config_ctrl_1));
	if(ret != 0){
		printk("Failed to write to I2C device address %x at Reg. %x \n", dev_lis2dw12.addr,config_ctrl_1[0]);
		return;
	}

    // IN CTRL 2 EVERYTHING IS AT DEFAULT (0), NO NEED TO WRITE ANYTHING

    // IN CTRL 3 EVERYTHING IS AT DEFAULT (0), NO NEED TO WRITE ANYTHING

    // CTRL 6
    uint8_t config_ctrl_6[2] = {CTRL6,LOW_NOISE_POS_MSK || FS_4G || BW_FILT_ODR_2};
    ret = i2c_write_dt(&dev_lis2dw12, config_ctrl_6, sizeof(config_ctrl_6));
	if(ret != 0){
		printk("Failed to write to I2C device address %x at Reg. %x \n", dev_lis2dw12.addr,config_ctrl_6[0]);
		return;
	}

    /*
        Now we need to configure the sensor by writing to the configuration register.
        configuration register has the address 0x03
        bit combination we need to use is 10001100 which is 0x8C
        This is the value we need to write to the configuration register
    */

    uint8_t config[2] = {STTS751_CONFIG_REG,0x8C};
	ret = i2c_write_dt(&dev_lis2dw12, config, sizeof(config));
	if(ret != 0){
		printk("Failed to write to I2C device address %x at Reg. %x \n", dev_lis2dw12.addr,config[0]);
		return;
	}

    /*
        Once the sensor is configured, 
        reading the temperature is a straightforward task of reading the two registers 
        temperature value high byte and temperature value low byte

        In order to read the registers, 
        we first need to write the address and then issue a read. 
        We will use the i2c_write_read_dt() API to do that in one shot
    */

   while (1){
        uint8_t temp_reading[2]= {0};
        uint8_t sensor_regs[2] ={STTS751_TEMP_LOW_REG,STTS751_TEMP_HIGH_REG};
        ret = i2c_write_read_dt(&dev_lis2dw12,&sensor_regs[0],1,&temp_reading[0],1);
        if(ret != 0){
            printk("Failed to write/read I2C device address %x at Reg. %x \r\n", dev_lis2dw12.addr,sensor_regs[0]);
        }
        ret = i2c_write_read_dt(&dev_lis2dw12,&sensor_regs[1],1,&temp_reading[1],1);
        if(ret != 0){
            printk("Failed to write/read I2C device address %x at Reg. %x \r\n", dev_lis2dw12.addr,sensor_regs[1]);
        }

        /*
            Now that we have the temperature reading in raw bytes, the next step to do is to convert them to Celsius and Fahrenheit
        */

        int temp = ((int)temp_reading[1] * 256 + ((int)temp_reading[0] & 0xF0)) / 16;
        if(temp > 2047)
        {
            temp -= 4096;
        }
        // Convert to engineering units 
        double cTemp = temp * 0.0625;
        double fTemp = cTemp * 1.8 + 32;
        //Print reading to console  
        printk("Temperature in Celsius : %.2f C \n", cTemp);
        printk("Temperature in Fahrenheit : %.2f F \n", fTemp);

        /*
            Build the application and flash it on your development kit. Using a serial terminal you should see the below output
        */
   }

   return 1
    
}
