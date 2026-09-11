#pragma once
#include <SDL2/SDL.h>
#include <stdexcept>
#include <string>
#include <cstdlib>

namespace hello {
inline SDL_Window *window = nullptr;
inline SDL_Renderer *renderer = nullptr;
inline void open(std::string const &title) {
  if (SDL_Init(SDL_INIT_VIDEO) != 0) throw std::runtime_error(SDL_GetError());
  window = SDL_CreateWindow(title.c_str(), SDL_WINDOWPOS_CENTERED,
                            SDL_WINDOWPOS_CENTERED, 640, 400, 0);
  if (!window) throw std::runtime_error(SDL_GetError());
  renderer = SDL_CreateRenderer(window, -1, SDL_RENDERER_SOFTWARE);
  if (!renderer) throw std::runtime_error(SDL_GetError());
  if (std::getenv("HELLO_SMOKE")) {
    SDL_Event key{}; key.type = SDL_KEYDOWN; key.key.keysym.sym = SDLK_SPACE;
    if (SDL_PushEvent(&key) < 0) throw std::runtime_error(SDL_GetError());
  }
}
inline int event() {
  SDL_Event e;
  while (SDL_PollEvent(&e)) {
    if (e.type == SDL_QUIT || (e.type == SDL_KEYDOWN && e.key.keysym.sym == SDLK_ESCAPE)) return 1;
    if (e.type == SDL_MOUSEBUTTONDOWN || (e.type == SDL_KEYDOWN && e.key.keysym.sym == SDLK_SPACE)) return 2;
  }
  return 0;
}
inline void draw(int red, int green, int blue) {
  SDL_SetRenderDrawColor(renderer, 246, 242, 231, 255);
  SDL_RenderClear(renderer);
  SDL_SetRenderDrawColor(renderer, red, green, blue, 255);
  SDL_Rect block{220, 100, 200, 200};
  SDL_RenderFillRect(renderer, &block);
  SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);
  SDL_Rect left{265, 155, 20, 20}, right{355, 155, 20, 20}, smile{275, 225, 90, 12};
  SDL_RenderFillRect(renderer, &left); SDL_RenderFillRect(renderer, &right);
  SDL_RenderFillRect(renderer, &smile);
  SDL_RenderPresent(renderer);
}
inline bool smoke() { return std::getenv("HELLO_SMOKE") != nullptr; }
inline void snapshot() {
  auto *surface = SDL_CreateRGBSurfaceWithFormat(0, 640, 400, 32, SDL_PIXELFORMAT_ARGB8888);
  if (!surface) throw std::runtime_error(SDL_GetError());
  if (SDL_RenderReadPixels(renderer, nullptr, surface->format->format, surface->pixels, surface->pitch) != 0 || SDL_SaveBMP(surface, "hello.bmp") != 0)
    throw std::runtime_error(SDL_GetError());
  SDL_FreeSurface(surface);
}
inline void pause() { SDL_Delay(16); }
inline void close() { SDL_DestroyRenderer(renderer); SDL_DestroyWindow(window); SDL_Quit(); }
}
