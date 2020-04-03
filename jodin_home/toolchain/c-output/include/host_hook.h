#ifndef __HOST_HOOK__HEADER__
#define __HOST_HOOK__HEADER__

/*

This file will be the "hook hook", which is basically the list of functions that the host will have to
do, as it requires system interaction of some kind

*/

void printf(const char* c);
void free(void* ptr);
void exit(int code);

#endif