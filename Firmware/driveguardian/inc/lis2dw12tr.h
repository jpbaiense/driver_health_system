/*
      LIS2DW12TR Sensor Driver
      Author: João Pedro Baiense
      Date: 29/12/2023
      based on: 
      https://github.com/nrfconnect/sdk-zephyr/tree/v2.4.99-ncs1/drivers/sensor/lis2dw12
      https://wiki.dfrobot.com/Gravity_I2C_LIS2DW12_Triple_Axis_Accelerometer_SKU_SEN0409
      https://github.com/STMicroelectronics/lis2dw12-pid
      https://www.youtube.com/watch?v=prZomlfA_bI
      https://www.youtube.com/watch?v=_JQAve05o_0&t=2047s
*/

/* HEADER FILES */

#include <zephyr/kernel.h>
#include <zephyr/device.h>
#include <zephyr/devicetree.h>
#include <zephyr/drivers/i2c.h> // header file of the I2C API
#include <zephyr/logging/log.h>
#include <math.h>
#include <stdio.h>
#include <stdint.h>

/* REGISTER MAP DEFINITIONS */

#define OUT_T_L     0x0D // Temp sensor output
#define OUT_T_H     0x0E // Temp sensor output
#define WHO_AM_I     0x0F //  Who am I ID -  Its value is fixed at 44h
#define CTRL1     0x20 // Control registers
#define CTRL2     0x21 // Control registers
#define CTRL3     0x22 // Control registers
#define CTRL4_INT1_PAD_CTRL     0x23 // Control registers 
#define CTRL5_INT2_PAD_CTRL     0x24 // Control registers
#define CTRL6     0x25 // Control registers
#define OUT_T     0x26 // Temp sensor output
#define STATUS     0x27 //  Status data register
#define OUT_X_L     0x28 // Output registers
#define OUT_X_H     0x29 // Output registers
#define OUT_Y_L     0x2A // Output registers
#define OUT_Y_H     0x2B // Output registers
#define OUT_Z_L     0x2C // Output registers
#define OUT_Z_H     0x2D // Output registers
#define FIFO_CTRL     0x2E //  FIFO control register
#define FIFO_SAMPLES     0x2F // Unread samples stored in FIFO
#define TAP_THS_X     0x30 // Tap thresholds
#define TAP_THS_Y     0x31 // Tap thresholds
#define TAP_THS_Z     0x32 // Tap thresholds
#define INT_DUR     0x33 // Interrupt duration
#define WAKE_UP_THS     0x34 // Tap/double-tap selection, inactivity enable, wakeup threshold
#define WAKE_UP_DUR     0x35 // Wakeup duration
#define FREE_FALL     0x36 //  Free-fall configuration
#define STATUS_DUP     0x37 //  Status register
#define WAKE_UP_SRC     0x38 //  Wakeup source
#define TAP_SRC     0x39 // Tap source
#define SIXD_SRC     0x3A //  6D source
#define ALL_INT_SRC     0x3B
#define X_OFS_USR     0x3C
#define Y_OFS_USR     0x3D
#define Z_OFS_USR     0x3E
#define CTRL_REG7     0x3F

/********************Bits Definition*********************/

/*
  @brief: CTRL1 register bits
  @default: 00000000
  */

/*Low-power mode selection Table 32*/
#define LP_MODE_POS                    (0U)
#define LP_MODE_MSK                    (0x03UL << LP_MODE_POS)
#define LP_MODE1                       ~(LP_MODE_MSK)
#define LP_MODE2                       (0x01UL << LP_MODE_POS)
#define LP_MODE3                       (0x02UL << LP_MODE_POS)
#define LP_MODE4                       (LP_MODE_MSK)


/*Mode selection Table 31*/
#define MODE_POS                       (2U)
#define MODE_MSK                       (0x03UL << MODE_POS)
#define LOW_POWER_MODE                 ~(MODE_MSK)
#define HIGH_PERFORMANCE_MODE          (0x01UL << MODE_POS)
#define SINGLE_DATA_CONVERSION_MODE    (0x02UL << MODE_POS)


/*Data rate configuration Table 30*/
#define ODR_POS                        (4U)
#define ODR_MSK                        (0x0FUL << ODR_POS)
#define POWER_DOWN                     ~(ODR_MSK)
#define LOW_POWER_MODE_1_6HZ           (0x01UL << ODR_POS)
#define LOW_POWER_MODE_12_5HZ          (0x02UL << ODR_POS)
#define LOW_POWER_MODE_25HZ            (0x03UL << ODR_POS)
#define LOW_POWER_MODE_50HZ            (0x04UL << ODR_POS)
#define LOW_POWER_MODE_100HZ           (0x05UL << ODR_POS)
#define LOW_POWER_MODE_200HZ           (0x06UL << ODR_POS)
#define LOW_POWER_MODE_400HZ           (0x07UL << ODR_POS)
#define LOW_POWER_MODE_800HZ           (0x08UL << ODR_POS)
#define LOW_POWER_MODE_1600HZ          (0x09UL << ODR_POS)

/*
  @brief: CTRL2 register
  @default: 00000100
  */

#define SIM_POS                        (0U)
#define SIM_MSK                        (0x01UL << SIM_POS)

#define I2C_DISABLE_POS                (1U)
#define I2C_DISABLE_MSK                (0x01UL << I2C_DISABLE_POS)

#define IF_ADD_INC_POS                 (2U)
#define IF_ADD_INC_MSK                 (0x01UL << IF_ADD_INC_POS)

#define BDU_POS                        (3U)
#define BDU_MSK                        (0x01UL << BDU_POS)

#define CS_PU_DISC_POS                 (4U)
#define CS_PU_DISC_MSK                 (0x01UL << CS_PU_DISC_POS)

#define SOFT_RESET_POS                 (6U)
#define SOFT_RESET_MSK                 (0x01UL << SOFT_RESET_POS)

#define BOOT_POS                       (7U)
#define BOOT_MSK                       (0x01UL << BOOT_POS)

/*
  @brief: CTRL3 register
  @default: 00000000
  */

#define SLP_MODE1_POS                  (0U)
#define SLP_MODE1_MSK                  (0x01UL << SLP_MODE1_POS)

#define SLP_MODE_SEL_POS               (1U)
#define SLP_MODE_SEL_MSK               (0x01UL << SLP_MODE_SEL_POS)

#define H_LACTIVE_POS                  (3U)
#define H_LACTIVE_MSK                  (0x01UL << H_LACTIVE_POS)

#define LIR_POS                        (4U)
#define LIR_MSK                        (0x01UL << LIR_POS)

#define PP_OD_POS                      (5U)
#define PP_OD_MSK                      (0x01UL << PP_OD_POS)

#define SELF_TEST_POS                  (6U)
#define SELF_TEST_MSK                  (0x03UL << SELF_TEST_POS)
#define NORMAL_MODE                    ~(SELF_TEST_MSK)
#define POS_SELF_TEST                  (0x01UL << SELF_TEST_POS)
#define NEG_SELF_TEST                  (0x02UL << SELF_TEST_POS)


/*
  @brief: CTRL6 register
  @default: 00000000
  */

#define LOW_NOISE_POS                  (2U)
#define LOW_NOISE_POS_MSK              (0x01UL << LOW_NOISE_POS)

#define FDS_POS                        (3U)
#define FDS_MSK                        (0x01UL << FDS_POS)

#define FS_POS                         (4U)
#define FS_MSK                         (0x03UL << FS_POS)
#define FS_2G                          ~(FS_MSK)
#define FS_4G                          (0x01UL << FS_POS)
#define FS_8G                          (0x02UL << FS_POS)
#define FS_16G                         (0x03UL << FS_POS)

#define BW_FILT_POS                    (6U)
#define BW_FILT_MSK                    (0x03UL << BW_FILT_POS)
#define BW_FILT_ODR_2                  ~(BW_FILT_MSK)
#define BW_FILT_ODR_4                  (0x01UL << BW_FILT_POS)
#define BW_FILT_ODR_10                 (0x02UL << BW_FILT_POS)
#define BW_FILT_ODR_20                 (0x03UL << BW_FILT_POS)

/*
  @brief: FIFO_CTRL register
  @default: 00000000
  */

#define FTH_POS                        (0U)
#define FTH_MSK                        (0x1F << FTH_POS)

#define FMODE_POS                      (5U)
#define FMODE_MSK                      (0x07UL << FMODE_POS)
#define FMODE_BYPASS                   ~(FMODE_MSK)
#define FMODE_FIFO                     (0x01UL << FMODE_POS)
#define FMODE_STREAM_TO_FIFO           (0x03UL << FMODE_POS)
#define FMODE_BYPASS_TO_STREAM         (0x04UL << FMODE_POS)
#define FMODE_STREAM                   (0x06UL << FMODE_POS)

uint8_t lis2dw12_init();
uint32_t get_x_axes_data(void);
uint32_t get_y_axes_data(void);
uint32_t get_z_axes_data(void);
