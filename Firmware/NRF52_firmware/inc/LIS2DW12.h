/* @file LIS2DW12.h
   @brief header file for LIS2DW12 src file
   @author bheesma-10
   https://github.com/leagueofassassins/NRF52_BLE_CENTRAL-ESP32_BLE_PERIPHERAL/blob/main/BLE_Peripheral_NRF52/main/LIS2DW12.h
*/


/* 
  #include "driver/gpio.h"
  #include "driver/i2c.h"
  #include  "esp_err.h"
  #include  "esp_log.h"
  #include  "esp_system.h"
*/

#include <stdint.h>

/***********************Slave Address************************/

#define LIS2DW12_ADDRESS                0x19

/********************Register Definitions********************/


/*Read-only*/
#define OUT_T_L                          0x0D
#define OUT_T_H                          0x0E
#define WHO_AM_I                         0x0F
#define WHO_AM_I_VALUE                         0x44
#define OUT_T                            0x26
#define STATUS                           0x27
#define OUT_X_L                          0x28
#define OUT_X_H                          0x29
#define OUT_Y_L                          0x2A
#define OUT_Y_H                          0x2B
#define OUT_Z_L                          0x2C
#define OUT_Z_H                          0x2D

#define FIFO_SAMPLES                     0x2F

#define STATUS_DUP                       0x37
#define WAKE_UP_SRC                      0x38
#define TAP_SRC                          0x39
#define SIXD_SRC                         0x3A
#define ALL_INT_SRC                      0x3B


/*Read-Write both*/
#define CTRL1                            0x20
#define CTRL2                            0x21
#define CTRL3                            0x22
#define CTRL4_INT1_PAD_CTRL              0x23
#define CTRL5_INT2_PAD_CTRL              0x24
#define CTRL6                            0x25

#define FIFO_CTRL                        0x2E

#define TAP_THS_X                        0x30
#define TAP_THS_Y                        0x31
#define TAP_THS_Z                        0x32
#define INT_DUR                          0x33
#define WAKE_UP_THS                      0x34
#define WAKE_UP_DUR                      0x35
#define FREE_FALL                        0x36

#define X_OFS_USR                        0x3C
#define Y_OFS_USR                        0x3D
#define Z_OFS_USR                        0x3E
#define CTRL7                            0x3F


/********************Bits Definition*********************/

/*
  @brief: CTRL1 register bits
  @default: 00000000
  */

/*Low-power mode selection*/
#define LP_MODE_POS                    (0U)
#define LP_MODE_MSK                    (0x03UL << LP_MODE_POS)
#define LP_MODE1                       ~(LP_MODE_MSK)
#define LP_MODE2                       (0x01UL << LP_MODE_POS)
#define LP_MODE3                       (0x02UL << LP_MODE_POS)
#define LP_MODE4                       (LP_MODE_MSK)


/*Mode selection*/
#define MODE_POS                       (2U)
#define MODE_MSK                       (0x03UL << MODE_POS)
#define LOW_POWER_MODE                 ~(MODE_MSK)
#define HIGH_PERFORMANCE_MODE          (0x01UL << MODE_POS)
#define SINGLE_DATA_CONVERSION_MODE    (0x02UL << MODE_POS)


/*Data rate configuration*/
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

/*X,Y,Z axes raw data*/
union {
  uint16_t x_axes_raw;
  uint8_t  X_OUT[2];
}raw_X_axesvalue;

union{
  uint16_t y_axes_raw;
  uint8_t  Y_OUT[2];
}raw_Y_axesvalue;

union{
  uint16_t z_axes_raw;
  uint8_t  Z_OUT[2];
}raw_Z_axesvalue;


/*functions*/
int i2c_init(void);
void i2c_write_reg(uint8_t register_address,uint8_t data);
uint8_t i2c_read_reg(uint8_t register_address);
void lis2dw12_init(const char* TAG);
uint8_t get_x_axes_data(void);
uint8_t get_y_axes_data(void);
uint8_t get_z_axes_data(void);