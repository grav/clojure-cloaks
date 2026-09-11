use std::ffi::{CStr, CString};
use std::{env, ptr};

mod sci {
    use std::ffi::{c_char, c_void};

    #[link(name = "sci")]
    unsafe extern "C" {
        pub fn graal_create_isolate(
            params: *const c_void,
            isolate: *mut *mut c_void,
            thread: *mut *mut c_void,
        ) -> i32;
        pub fn eval_string(thread: i64, expression: *const c_char) -> *const c_char;
        pub fn graal_tear_down_isolate(thread: *mut c_void) -> i32;
    }
}

fn main() -> Result<(), Box<dyn std::error::Error>> {
    let expression = CString::new(env::args().nth(1).ok_or("Usage: sci-eval EXPRESSION")?)?;
    let mut isolate = ptr::null_mut();
    let mut thread = ptr::null_mut();
    // These declarations match the generated libsci.h and graal_isolate.h.
    unsafe {
        if sci::graal_create_isolate(ptr::null(), &mut isolate, &mut thread) != 0 {
            return Err("Could not create the SCI runtime".into());
        }
        let result = sci::eval_string(thread as i64, expression.as_ptr());
        let output = if result.is_null() {
            None
        } else {
            // Copy the result before destroying the runtime that owns it.
            Some(CStr::from_ptr(result).to_string_lossy().into_owned())
        };
        if sci::graal_tear_down_isolate(thread) != 0 {
            return Err("Could not shut down the SCI runtime".into());
        }
        println!("{}", output.ok_or("SCI returned a null pointer")?);
    }
    Ok(())
}
