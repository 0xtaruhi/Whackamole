/*
 * File: main.rs
 * Author: 0xtaruhi (zhang_zhengyi@outlook.com)
 * File Created: Thursday, 15th June 2023 6:56:52 pm
 * Last Modified: Saturday, 17th June 2023 10:48:12 am
 * Copyright: 2023 - 2023 Fudan University
 */
mod coe;

use coe::coe::Coe;
use image::{DynamicImage, GenericImageView};
use std::env;

fn write_coe(img: &DynamicImage, coe_path: &str) -> Result<(), Box<dyn std::error::Error>> {
    let (width, height) = img.dimensions();
    println!("Image dimensions: {}x{}", width, height);

    let mut data: Vec<u16> = Vec::new();
    for y in 0..height {
        for x in 0..width {
            let pixel = img.get_pixel(x, y);
            let r = (pixel[0] >> 3) & 0x1f;
            let g = (pixel[1] >> 3) & 0x1f;
            let b = (pixel[2] >> 3) & 0x1f;
            let a = (pixel[3] >> 7) & 0x01;
            let mut pixel: u16 = 0;
            pixel = pixel | (a as u16) << 15;
            pixel = pixel | (b as u16) << 10;
            pixel = pixel | (g as u16) << 5;
            pixel = pixel | (r as u16);
            data.push(pixel);
        }
    }

    let coe = Coe::new(coe_path, 16, height * width, data);
    coe.save()?;
    Ok(())
}

fn main() -> Result<(), Box<dyn std::error::Error>> {
    let args: Vec<String> = env::args().collect();
    if args.len() < 2 {
        println!("Usage: {} <image_path>", args[0]);
        return Ok(());
    }
    let image_path = &args[1];
    let mut store_path = args[2].clone();

    if std::path::Path::new(&store_path).is_dir() {
        let raw_image_name = image_path.split("/").last().unwrap().split(".").next().unwrap();
        store_path = format!("{}/{}.coe", store_path, raw_image_name);
    }

    let img = image::open(image_path.as_str()).map_err(|e| e.to_string())?;
    write_coe(&img, &store_path)?;
    Ok(())
}
