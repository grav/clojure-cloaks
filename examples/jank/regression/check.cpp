#include <cpptrace/cpptrace.hpp>
#include <fstream>
#include <iostream>
#include <iterator>
#include <stdexcept>
#include <vector>

int main(int argc, char** argv) {
    if(argc != 2) return 2;
    std::ifstream input(argv[1], std::ios::binary);
    std::vector<char> object((std::istreambuf_iterator<char>(input)), {});
    if(object.empty()) throw std::runtime_error("missing fixture");
    cpptrace::absorb_trace_exceptions(false);
    cpptrace::register_jit_object(object.data(), object.size());
    auto trace = cpptrace::raw_trace{{0x100004}}.resolve();
    cpptrace::unregister_jit_object(object.data());
    if(trace.frames.size() != 1) throw std::runtime_error("missing frame");
    const auto& frame = trace.frames.front();
    std::cout << frame.symbol << " " << frame.filename << ":" << frame.line.value_or(0) << '\n';
    if(frame.symbol != "arm64_jit_fixture" || frame.filename.find("fixture.cpp") == std::string::npos || !frame.line.has_value())
        throw std::runtime_error("JIT source location was not resolved");
}
