/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 ******************************************************************************/
/* Filename : $RCSfile$
 *
 * The declarations here have to be in a header file, 
 * because they need to be known both to the kernel 
 * module (in chardev.c) and the process calling ioctl 
 * (ioctl.c)
 */

/* $Log$
*
*/

#ifndef CHARDEV_H
#define CHARDEV_H

#define MAX_DEV 4

#include <linux/ioctl.h> 

/* These are the IOCTL value the pcmmio driver recognizes */

#define WRITE_DAC_DATA 		_IOWR(MAJOR_NUM, 0, int)

#define READ_DAC_STATUS 	_IOWR(MAJOR_NUM, 1, int)

#define WRITE_DAC_COMMAND 	_IOWR(MAJOR_NUM, 2, int)

#define WRITE_ADC_COMMAND	_IOWR(MAJOR_NUM, 3, int)

#define READ_ADC_DATA 		_IOWR(MAJOR_NUM, 4, int)

#define READ_ADC_STATUS		_IOWR(MAJOR_NUM, 5, int)

#define WRITE_DIO_BYTE 		_IOWR(MAJOR_NUM, 6, int)

#define READ_DIO_BYTE 		_IOWR(MAJOR_NUM, 7, int)

#define MIO_WRITE_REG 		_IOWR(MAJOR_NUM, 8, int)

#define MIO_READ_REG 		_IOWR(MAJOR_NUM, 9, int)

#define WAIT_A2D_INT_1 		_IOWR(MAJOR_NUM, 10, int)

#define WAIT_A2D_INT_2 		_IOWR(MAJOR_NUM, 11, int)

#define WAIT_DAC_INT_1 		_IOWR(MAJOR_NUM, 12, int)

#define WAIT_DAC_INT_2 		_IOWR(MAJOR_NUM, 13, int)

#define WAIT_DIO_INT 		_IOWR(MAJOR_NUM, 14, int)

#define READ_IRQ_ASSIGNED	_IOWR(MAJOR_NUM, 15, int)

#define DIO_GET_INT			_IOWR(MAJOR_NUM, 16, int)

/* The name of the device file */

#define DEVICE_FILE_NAME "pcmmio"

#endif

/* These are the error codes for mio_error_code */

#define MIO_SUCCESS 0
#define MIO_OPEN_ERROR 1
#define MIO_TIMEOUT_ERROR 2
#define MIO_BAD_CHANNEL_NUMBER 3
#define MIO_BAD_MODE_NUMBER 4
#define MIO_BAD_RANGE 5
#define MIO_COMMAND_WRITE_FAILURE 6
#define MIO_READ_DATA_FAILURE 7
#define MIO_MISSING_IRQ 8
#define MIO_ILLEGAL_VOLTAGE 9
#define MIO_BAD_DEVICE 10

/* These are DAC specific defines */

#define DAC_BUSY 0x80

#define DAC_SPAN_UNI5  0
#define DAC_SPAN_UNI10 1
#define DAC_SPAN_BI5   2
#define DAC_SPAN_BI10  3
#define DAC_SPAN_BI2   4
#define DAC_SPAN_BI7   5

/* These are ADC specific defines */

#define	ADC_SINGLE_ENDED 0x80
#define ADC_DIFFERENTIAL 0x00

#define ADC_UNIPOLAR  0x08
#define ADC_BIPOLAR   0x00

#define ADC_TOP_5V	  0x00
#define ADC_TOP_10V	  0x04

#define ADC_CH0_SELECT 0x00
#define ADC_CH1_SELECT 0x40
#define ADC_CH2_SELECT 0x10
#define ADC_CH3_SELECT 0x50
#define ADC_CH4_SELECT 0x20
#define ADC_CH5_SELECT 0x60
#define ADC_CH6_SELECT 0x30
#define ADC_CH7_SELECT 0x70

/* These are DIO specific defines */

#define FALLING 1
#define RISING  0

#ifdef LIB_DEFINED

/* These are used by the library functions */

int mio_error_code;
char mio_error_string[128];
float adc_bitval[MAX_DEV][16] = {.00, .00, .00, .00, .00, .00, .00, .00,
							     .00, .00, .00, .00, .00, .00, .00, .00,
								 .00, .00, .00, .00, .00, .00, .00, .00,
							     .00, .00, .00, .00, .00, .00, .00, .00,
								 .00, .00, .00, .00, .00, .00, .00, .00,
							     .00, .00, .00, .00, .00, .00, .00, .00,
								 .00, .00, .00, .00, .00, .00, .00, .00,
							     .00, .00, .00, .00, .00, .00, .00, .00};

unsigned short adc_adjust[MAX_DEV][16] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
										  0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
										  0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
										  0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

float adc_offset[MAX_DEV][16] = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
								  0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
								  0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
								  0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
								  0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
								  0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
								  0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
								  0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };

#else

/* The rest of this file is made up of global variables available to application
   code and the function prototypes for all available functions */

extern int mio_error_code;
extern char mio_error_string[128];
extern float adc_bitval[MAX_DEV][16];
extern unsigned short adc_adjust[MAX_DEV][16];
extern float adc_offset[MAX_DEV][16];

#endif

int disable_dio_interrupt(int dev_num);
int enable_dio_interrupt(int dev_num);
int disable_dac_interrupt(int dev_num, int dac_num);
int enable_dac_interrupt(int dev_num, int dac_num);
int disable_adc_interrupt(int dev_num, int adc_num);
int enable_adc_interrupt(int dev_num, int adc_num);
int set_dac_span(int dev_num, int channel, unsigned char span_value);
int wait_dac_ready(int dev_num, int channel);
int set_dac_output(int dev_num, int channel, unsigned short dac_value);
int set_dac_voltage(int dev_num, int channel, float voltage);
int set_dac_voltage(int dev_num, int channel, float voltage);
unsigned char read_dio_byte(int dev_num, int offset);
unsigned char mio_read_reg(int dev_num, int offset);
int mio_write_reg(int dev_num, int offset, unsigned char value);
int write_dio_byte(int dev_num, int offset, unsigned char value);
int write_dac_command(int dev_num, int dac_num,unsigned char value);
int adc_start_conversion(int dev_num, int channel);
float adc_get_channel_voltage(int dev_num, int channel);
int adc_convert_all_channels(int dev_num, unsigned short *buffer);
float adc_convert_to_volts(int dev_num, int channel, unsigned short value);
int adc_convert_single_repeated(int dev_num, int channel, unsigned short count, unsigned short *buffer);
int adc_buffered_channel_conversions(int dev_num, unsigned char *input_channel_buffer,unsigned short *buffer);
int adc_wait_ready(int dev_num, int channel);
int write_adc_command(int dev_num, int adc_num,unsigned char value);
int buffered_dac_output(int dev_num, unsigned char *cmd_buff,unsigned short *data_buff);
int write_dac_data(int dev_num, int dac_num, unsigned short value);
unsigned char dac_read_status(int dev_num, int dac_num);
unsigned char adc_read_status(int dev_num, int adc_num);
int adc_set_channel_mode(int dev_num, int channel, int input_mode,int duplex,int range);
unsigned short adc_read_conversion_data(int dev_num, int channel);
float adc_auto_get_channel_voltage(int dev_num, int channel);
int dio_read_bit(int dev_num, int bit_number);
int dio_write_bit(int dev_num, int bit_number, int val);
int dio_set_bit(int dev_num, int bit_number);
int dio_clr_bit(int dev_num, int bit_number);
int dio_enab_bit_int(int dev_num, int bit_number, int polarity);
int dio_disab_bit_int(int dev_num, int bit_number);
int dio_clr_int(int dev_num, int bit_number);
int dio_get_int(int dev_num);
int wait_adc_int(int dev_num, int adc_num);
int wait_dac_int(int dev_num, int dac_num);
int wait_dio_int(int dev_num);
