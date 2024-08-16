# SPDX-License-Identifier: Apache-2.0

cmake_minimum_required(VERSION 3.20.0)
find_package(Zephyr REQUIRED HINTS $ENV{ZEPHYR_BASE})
project(driveguardian_dk)

target_sources(app PRIVATE src/bmp581.c)
target_sources(app PRIVATE src/main.c)
target_sources(app PRIVATE src/lis2dw12tr.c)
target_sources(app PRIVATE src/bh1790glc.c)
target_sources(app PRIVATE src/sensor_data.c)
target_sources(app PRIVATE src/max17043.c)
target_include_directories(app PRIVATE inc)

