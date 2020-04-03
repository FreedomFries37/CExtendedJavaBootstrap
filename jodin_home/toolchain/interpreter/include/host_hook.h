#ifndef __HOST_HOOK__HEADER__
#define __HOST_HOOK__HEADER__

/*

This file will be the "interpreter hook", which is basically the list of functions that the interpreter will have to
do, as it requires system interaction of some kind

*/

void _interpreter_print(char * c);

// Input is path, output is file descriptor
int _open_file(std::String path);
void _flush_file(int fd);
void _close_file(int fd);
char _read_file(int fd, int position, bool* error);
bool _write_file(int fd, int position);


#endif