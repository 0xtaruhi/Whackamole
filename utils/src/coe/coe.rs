/*
 * File: coe.rs
 * Author: 0xtaruhi (zhang_zhengyi@outlook.com)
 * File Created: Thursday, 15th June 2023 12:18:22 pm
 * Last Modified: Saturday, 17th June 2023 10:48:17 am
 * Copyright: 2023 - 2023 Fudan University
 */
use std::io::Write;

pub struct Coe<T> {
    pub filename: String,
    pub width: u32,
    pub depth: u32,
    pub data: Vec<T>,
}

impl<T> Coe<T>
  where T: std::fmt::Binary + Copy
 {
    pub fn new(filename: &str, width: u32, depth: u32, data: Vec<T>) -> Self {
        Coe {
            filename: filename.to_string(),
            width,
            depth,
            data,
        }
    }

    pub fn save(&self) -> Result<(), Box<dyn std::error::Error>> {
        println!(
            "Saving {} as a {}-bit 2-radix coe file",
            self.filename, self.width
        );

        let mut file = std::fs::File::create(&self.filename)?;
        file.write_all(self.get_header().as_bytes())?;
        file.write_all(self.get_contents().as_bytes())?;
        Ok(())
    }

    fn get_header(&self) -> String {
        let mut header = String::new();
        header.push_str("memory_initialization_radix=2;\n");
        header.push_str("memory_initialization_vector=\n");
        header
    }

    fn get_contents(&self) -> String {
        let mut contents = String::new();
        for (i, pixel) in self.data.iter().enumerate() {
            contents.push_str(&format!("{:0width$b}", pixel, width = self.width as usize));
            if i < self.data.len() - 1 {
                contents.push_str(",\n");
            }
        }
        contents.push_str(";");
        contents
    }
}
