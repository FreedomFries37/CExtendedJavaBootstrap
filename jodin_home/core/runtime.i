/*
This file is mean to be added onto by the compiler. All non-dynamic declarations will occur here.

This file is compiled with special rules, significantly that words starting with __ are valid
*/

#define KB(X) (1028 * (X))
#define MB(X) (1028 * KB(X))
#define GB(X) (1028 * MB(X))

#ifndef HEAP_SIZE
#define HEAP_SIZE (MB(128))
#endif

#ifndef STRING_TYPE
#define STRING_TYPE char*
#endif

#include <string.h>
#include <primitives.h>

typedef unsigned char byte;



struct j_heap{
	byte* jheap;
	size_t size;
	size_t bytes_free;
#ifdef GC
	double allocation_percent;
#endif
};

struct j_heap jodin_heap;


void panic(STRING_TYPE message) {
	std::String fixed = new std::String(message); // whether the STRING type is a char* or a std::String, this always works
	println_s(fixed);
	exit(-1);
}


void assert_failure(const char* file, int linenumber, const char* message) {
	print(file);
	print(": ");
	//print()
	println(message);
}


bool __init_heap() {


	jodin_heap.jheap = nullptr;
	if(jodin_heap.jheap == nullptr) {
		return false;
	}
	jodin_heap.size = HEAP_SIZE;
	jodin_heap.bytes_free = 0;
#ifdef GC
	jodin_heap.allocation_percent = 0;
#endif
	return true;
}

void __free_heap() {
	free(jodin_heap.jheap);
}






