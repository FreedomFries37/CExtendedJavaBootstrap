#ifndef __ASSERT_HEADER__
#define __ASSERT_HEADER__

assert_failure(const char* file, int linenumber, const char* message);

#ifdef NDEBUG
#define assert(ingore) ((void) 0)
#else


#define assert(expr) \
do {\
	if(!expr) {\
		assert_failure(__FILE__, __LINE__, #expr);\
	}\
while(0)

#endif