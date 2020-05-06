

#ifndef __PANIC__HEADER__
#define __PANIC__HEADER__

void panic(std::String msg, int line, char* filename);

#ifndef FORCE_STRING
#define FORCE_STRING(string) (new std::String(string))
#endif
#define PANIC(msg) panic(FORCE_STRING(msg), __LINE__, __FILE__)

#endif