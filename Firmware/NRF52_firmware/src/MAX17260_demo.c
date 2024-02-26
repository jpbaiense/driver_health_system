// max17260.c

#include <zephyr/kernel.h>
#include <zephyr/device.h>
#include <zephyr/devicetree.h>
#include <zephyr/drivers/sensor.h>


int main(void)
{
	const struct device *const dev = DEVICE_DT_GET_ONE(maxim_max17262);

	if (!device_is_ready(dev)) {
		printk("sensor: device not ready.\n");
		return 0;
	}

	while (1) {
		struct sensor_value voltage, avg_current, temperature;
		float i_avg;

		sensor_sample_fetch(dev);
		sensor_channel_get(dev, SENSOR_CHAN_GAUGE_VOLTAGE, &voltage);
		sensor_channel_get(dev, SENSOR_CHAN_GAUGE_AVG_CURRENT,
						  &avg_current);
		sensor_channel_get(dev, SENSOR_CHAN_GAUGE_TEMP, &temperature);

		i_avg = avg_current.val1 + (avg_current.val2 / 1000000.0);

		printk("V: %d.%06d V; I: %f mA; T: %d.%06d °C\n",
		      voltage.val1, voltage.val2, (double)i_avg,
		      temperature.val1, temperature.val2);

		k_sleep(K_MSEC(1000));
	}
	return 0;
}

// Analog Voltage Measurement
#define REG_VCELL 0x09
#define REG_AVGVCELL 0x19
#define REG_MAXMINVOLT 0x1b

// Analog Current Measurement
#define REG_CURRENT 0x0a
#define REG_AVGCURRENT 0x0b
#define REG_MAXMINCURR 0x1c

// Analog temperature measurement
#define REG_TEMP 0x08
#define REG_AVGTA 0x16
#define REG_MAXMINTEMP 0x1a
#define REG_DIETEMP 0x34

// Alert Function
#define REG_VALRTTH 0x01
#define REG_TALRTTH 0x02
#define REG_SALRTTH 0x03
#define REG_IALERTTH 0xb4


// Empty Hold And 99% Hold
#define REG_SOCHOLD 0xd3

// Lithium Iron Phosphate Support
#define REG_SCOCVLIM 0xd1

// Current Measurements
#define REG_CGAIN 0x2e
#define REG_COFF 0x2f

// Copper Trace Current Testing
#define REG_CURVE 0xb9
#define REG_CGTEMPCO 0xb8

// Temperature Measurements
#define REG_AIN 0x27
#define REG_TGAIN 0x2c
#define REG_TOFF 0x2d

// Dynamic Power Output Registers
#define REG_MPPCURRENT 0xd9
#define REG_SPPCURRENT 0xda
#define REG_MAXPEAKPOWER 0xd4
#define REG_SUSPEAKPOWER 0xd5

// Dynamic Power Configuration Registers
#define REG_PACKRESISTANCE 0xd6
#define REG_SYSRESISTANCE 0xd7
#define REG_MINSYSVOLTAGE 0xd8
#define REG_RGAIN 0x43

// ModelGauge m5 Algorithm Battery Parameters
#define REG_VEMPTY 0x3a
#define REG_DESIGNCAP 0x18
#define REG_MODELCFG 0xdb
#define REG_ICHGTERM 0x1e
#define REG_FULLSOCTHR 0x13
#define REG_OCVTABLE0 0x80
#define REG_OCVTABLE15 0x8f
#define REG_XTABLE0 0x90
#define REG_XTABLE15 0x9f
#define REG_QRTABLE00 0x12
#define REG_QRTABLE10 0x22
#define REG_QRTABLE20 0x32
#define REG_QRTABLE30 0x42
#define REG_RCOMP0 0x38
#define REG_TEMPCO 0x39

// ModelGauge m5 Output Registers
#define REG_REPCAP 0x05
#define REG_ATAVCAP 0xdf
#define REG_REPSOC 0x06
#define REG_ATAVSOC 0xde
#define REG_FULLCAPREP 0x10
#define REG_FULLCAP 0x35
#define REG_FULLCAPNOM 0x23
#define REG_TTE 0x11
#define REG_ATTTE 0xdd
#define REG_TTF 0x20
#define REG_CYCLES 0x17
#define REG_STATUS 0x00
#define REG_AGE 0x07
#define REG_TIMERH 0xbe
#define REG_TIMER 0x3e
#define REG_RCELL 0x14
#define REG_VRIPPLE 0xbc

// ModelGauge m5 Algorithm Configuration Registers
#define REG_ATRATE 0x04
#define REG_FILTERCFG 0x29
#define REG_RELAXCFG 0x2a
#define REG_LEARNCFG 0x28
#define REG_MISCCFG 0x2b
#define REG_CONVGCFG 0x49
#define REG_RIPPLECFG 0xbd

// ModelGauge m5 Algorithm Additional Registers
#define REG_DQACC 0x45
#define REG_DPACC 0x46
#define REG_QRESIDUAL 0x0c
#define REG_ATQRESIDUAL 0xdc
#define REG_VFSOC 0xff
#define REG_VFOCV 0xfb
#define REG_QH 0x4d
#define REG_AVCAP 0x1f
#define REG_AVSOC 0x0e
#define REG_MIXCAP 0x0f
#define REG_MIXSOC 0x0d
#define REG_VFREMCAP 0x4a
#define REG_FSTAT 0x3d

// Status And Configuration Registers
#define REG_CONFIG 0x1d
#define REG_CONFIG2 0xbb
#define REG_DEVNAME 0x21
#define REG_SHDNTIMER 0x3f
#define REG_STATUS2 0xb0
#define REG_HIBCFG 0xba
#define REG_SOFTWAKEUP 0x60

// Analog Power Data
#define REG_POWER 0xb1
#define REG_AVGPOWER 0xb3



#define REG_ID_USERMEM2 0xb2
#define REG_TTFCFG 0xb5
#define REG_CVMIXCAP 0xb6
#define REG_CVHALFTIME 0xb7

#define REG_RSENSE_USERMEM3 0xd0
#define REG_VGAIN 0xd2

#define SLAVE_ADDR 0x36

// Get relative state of charge in %
int8_t fg_get_rsoc(void) {
	uint16_t data;
	if(pic18_i2c_read(SLAVE_ADDR, REG_REPSOC, &data, 2) < 0)
		return -1;
	return data/256;
}
// Get relative state of health in %
int8_t fg_get_rsoh(void) {
	uint16_t soh;

	if(pic18_i2c_read(SLAVE_ADDR, REG_AGE, &soh, 2) < 0)
		return -1;
	return soh/256;
}


// *********** Conversion constants and macros ***********
// RSENSE mOhm
#define RSENSE  2
#define RegToVal(x, y)  (x * y / RSENSE)
// VCAP uV - coltage to convert capacity
#define VCAP    5
#define Reg2VCAP(x)    RegToVal(x, VCAP)
// VCURR uV - voltage to convert current
#define VCURR   1.5625
#define Reg2Curr(x)    RegToVal(x, VCURR)
// VPOW u(V*V) - coefficient to calculate power measurements
#define VPOW    8
#define Reg2Pow(x)     RegToVal(x, VPOW)
// VoltageStep
#define Reg2mV(x)    (x /12.8)

// Get remaining capacity of the battery in mA-h
int16_t fg_get_rcap(void) {
	uint16_t data;

    if(pic18_i2c_read(SLAVE_ADDR, REG_REPCAP, &data, 2) < 0)
            return -1;
    return Reg2VCAP(data);
}
// Get full capacity of the battery in mA-h
int16_t fg_get_fcap(void) {
	uint16_t data;

    if(pic18_i2c_read(SLAVE_ADDR, REG_FULLCAPREP, &data, 2) < 0)
            return -1;
    return Reg2VCAP(data);
}

// Get nominal  full capacity of the battery in mA-h
int16_t fg_get_fcap_nom(void) {
	uint16_t data;

    if(pic18_i2c_read(SLAVE_ADDR, REG_FULLCAPNOM, &data, 2) < 0)
            return -1;
    return Reg2VCAP(data);
}

// Get full capacity of the battery in mA-h
int16_t fg_get_vcel(TBool average) {
	uint16_t data;
    if(pic18_i2c_read(SLAVE_ADDR, average?REG_AVGVCELL:REG_VCELL, &data, 2) < 0)
            return -1;
    return Reg2mV(data);
}

int16_t fg_get_curr(TBool average) {
	int16_t data;

    if(pic18_i2c_read(SLAVE_ADDR, average?REG_AVGCURRENT:REG_CURRENT, &data, 2) < 0)
        return -1;
    return Reg2Curr(data);
}

// Get power in mW
int16_t fg_get_power(TBool average) {
	int16_t data;

    if(pic18_i2c_read(SLAVE_ADDR, average?REG_AVGPOWER:REG_POWER, &data, 2) < 0)
        return -1;
    return Reg2Pow(data);
}

typedef union {
    uint16_t data;
    struct {
        unsigned unused2: 1;
        unsigned por    : 1;
        unsigned imn    : 1;
        unsigned bst    : 1;
        unsigned unused1: 2;
        unsigned lmx    : 1;
        unsigned dsoci  : 1;
        unsigned vmn    : 1;
        unsigned tmn    : 1;
        unsigned smn    : 1;
        unsigned bi     : 1;
        unsigned vmx    : 1;
        unsigned tmx    : 1;
        unsigned smx    : 1;
        unsigned br     : 1;
    };
} fg_status_t;

#define ReadRegister(addr, val)  { pic18_i2c_read(SLAVE_ADDR, addr, val, 2);}
uint16_t fg_tx_val;
#define WriteRegister(addr, val) { fg_tx_val = val; pic18_i2c_write(SLAVE_ADDR, addr, &fg_tx_val, 2);}
// Design capacity measured in 0.5ma-h units in FG. 
#define DesignCapacity  (3700 / 2)
// 250mA on 10mOhm
//#define IchgTerm        (0x0640)
// 200mA on 2mOhm
#define IchgTerm        (0x0140)

// 3.3/3.88
#define Vempty          (0xA561)
extern void Delay(uint16_t t);
void fg_init(void){
    fg_status_t _st;
    ReadRegister(REG_STATUS, &_st.data);
    if(_st.por || _st.br){   
        uint16_t HibCFG;
        _st.por = 0;
        _st.br = 0;
        WriteRegister(REG_STATUS, _st.data);
        ReadRegister(REG_HIBCFG, &HibCFG);
        WriteRegister(REG_SOFTWAKEUP, 0x90);
        WriteRegister(REG_HIBCFG, 0x00);
        WriteRegister(REG_SOFTWAKEUP, 0x90);

        WriteRegister(REG_DESIGNCAP, DesignCapacity);
        WriteRegister(REG_ICHGTERM,IchgTerm);
        WriteRegister(REG_VEMPTY,Vempty);

        WriteRegister (REG_MODELCFG , 0x8000) ;
        uint16_t _wait = 0x8000;
        while (_wait){
            ReadRegister(REG_MODELCFG, &_wait);
            _wait &=0x8000;
            Delay(10);
        }
        WriteRegister (REG_HIBCFG , HibCFG);
        ReadRegister(REG_STATUS, &_st.data);
    
    }
    NOP();
//    // Now we can read everything
//    uint16_t fcap = fg_get_fcap();
//    uint16_t rcap = fg_get_rcap();
//    uint16_t rsoc = fg_get_rsoc();
//    uint16_t rsoh = fg_get_rsoh();
  
}