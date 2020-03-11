#ifndef SANDBOX_STRING_H
#define SANDBOX_STRING_H

#include <primitives.h>
#define NULL ((void*) 0)



char * strcpy ( char * destination, const char * source );
char * strncpy ( char * destination, const char * source, size_t num );

char * strcat ( char * destination, const char * source );
char * strncat ( char * destination, const char * source, size_t num );


int strcmp ( const char * str1, const char * str2 );
int strcoll ( const char * str1, const char * str2 );
int strncmp ( const char * str1, const char * str2, size_t num );
size_t strxfrm ( char * destination, const char * source, size_t num );

char* strdup (const char* str1);
size_t strlen ( const char * str );



#endif //SANDBOX_STRING_H
