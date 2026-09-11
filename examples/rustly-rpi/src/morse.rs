// Allocation-free Morse sequencer. Timing is independent of UART polling.
pub const MAX_INPUT: usize = 64;
const MAX_SEGMENTS: usize = MAX_INPUT * 12;

#[derive(Clone, Copy, Debug, PartialEq)]
pub struct Segment { pub on: bool, pub units: u8 }
const OFF: Segment = Segment { on: false, units: 0 };

pub struct Morse {
    segments: [Segment; MAX_SEGMENTS],
    len: usize,
    index: usize,
    started: u64,
}

impl Morse {
    pub const fn new() -> Self {
        Self { segments: [OFF; MAX_SEGMENTS], len: 0, index: 0, started: 0 }
    }

    pub fn set(&mut self, input: &[u8], alphabet: &[&[u8]; 36], now: u64) -> Result<(), ()> {
        // Validate before replacing the current message.
        if input.len() > MAX_INPUT || input.iter().any(|b| !b.is_ascii_alphanumeric() && *b != b' ') {
            return Err(());
        }
        self.len = 0;
        self.index = 0;
        self.started = now;
        let mut word_gap = false;
        for &byte in input {
            if byte == b' ' { word_gap = true; continue; }
            if self.len > 0 {
                self.segments[self.len - 1].units = if word_gap { 7 } else { 3 };
            }
            word_gap = false;
            let i = if byte.is_ascii_alphabetic() { (byte.to_ascii_lowercase() - b'a') as usize }
                    else { (byte - b'0') as usize + 26 };
            for &symbol in alphabet[i] {
                self.segments[self.len] = Segment { on: true, units: if symbol == b'-' { 3 } else { 1 } };
                self.segments[self.len + 1] = Segment { on: false, units: 1 };
                self.len += 2;
            }
        }
        if self.len > 0 { self.segments[self.len - 1].units = 7; }
        Ok(())
    }

    pub fn output(&mut self, now: u64, unit: u64) -> bool {
        if self.len == 0 { return false; }
        // Advance at most one edge per poll, preserving visible pulse lengths
        // if serial output temporarily delays the main loop.
        if now.wrapping_sub(self.started) >= unit * self.segments[self.index].units as u64 {
            self.index = (self.index + 1) % self.len;
            self.started = now;
        }
        self.segments[self.index].on
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    fn alphabet() -> [&'static [u8]; 36] {
        let mut a: [&[u8]; 36] = [b"."; 36];
        a[2] = b"-.-."; a[11] = b".-.."; a[9] = b".---";
        a
    }
    #[test]
    fn clj_timing_and_repeat() {
        let mut m = Morse::new();
        m.set(b"clj", &alphabet(), 0).unwrap();
        let expected = [(true,3),(false,1),(true,1),(false,1),(true,3),(false,1),(true,1),(false,3),
                        (true,1),(false,1),(true,3),(false,1),(true,1),(false,1),(true,1),(false,3),
                        (true,1),(false,1),(true,3),(false,1),(true,3),(false,1),(true,3),(false,7)];
        let mut now = 0;
        for (on, units) in expected {
            assert_eq!(m.output(now, 10), on);
            assert_eq!(m.output(now + units*10 - 1, 10), on);
            now += units*10;
        }
        assert!(m.output(now, 10)); // repeat begins with C's dash
        assert_eq!(m.index, 0);
    }
    #[test]
    fn words_replacement_stop_and_invalid_input() {
        let mut m = Morse::new();
        m.set(b" C  J ", &alphabet(), 0).unwrap();
        assert_eq!(m.segments[7].units, 7);
        let old_len = m.len;
        assert!(m.set("Björk".as_bytes(), &alphabet(), 0).is_err());
        assert_eq!(m.len, old_len);
        m.set(b"l", &alphabet(), 20).unwrap();
        assert_eq!(m.len, 8);
        assert!(m.output(20, 10));
        m.set(b"   ", &alphabet(), 30).unwrap();
        assert!(!m.output(30, 10));
    }
    #[test]
    fn capacity_and_clock_wrap() {
        let mut m = Morse::new();
        let a: [&[u8];36] = [b"-----";36];
        m.set(&[b'0';MAX_INPUT], &a, u64::MAX-5).unwrap();
        assert_eq!(m.len, 640);
        assert!(m.set(&[b'0';MAX_INPUT+1], &a, 0).is_err());
        assert!(m.output(u64::MAX-1, 10));
        assert!(!m.output(24, 10));
    }
}
