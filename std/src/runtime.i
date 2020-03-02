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

#include <string.h>

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


void panic(std::String message) {
	println_s(message);
	exit(-1);
}



bool __init_heap() {
	jodin_heap.jheap = calloc(sizeof(byte), HEAP_SIZE);
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

void* __jalloc(size_t length) {
	return malloc(length);
}




