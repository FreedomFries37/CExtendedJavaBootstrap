#ifndef __STDIO__HEADER__
#define __STDIO__HEADER__

#include <primitives.h>
#include <system/filesystem.h>

typedef std::File FILE;
typedef u64 fpos_t;

#define NULL nullptr;
#define EOF -1;


#define stdin (new FILE(0))
#define stdout (new FILE(1))
#define stderr (new FILE(2))

int fclose(FILE stream);
int clearerr(FILE stream);
int feof(FILE stream);
int ferror(FILE stream);
int fflush(FILE stream);

int fgetpos(FILE stream, fpos_t* pos);
FILE fopen(const char* filename, const char* mode);


#endif