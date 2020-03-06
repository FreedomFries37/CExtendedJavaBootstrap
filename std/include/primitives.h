
#ifndef SANDBOX_PRIMITIVES_H
#define SANDBOX_PRIMITIVES_H

typedef char i8;
typedef unsigned char u8;
typedef short int i16;
typedef unsigned short int u16;
typedef int i32;
typedef unsigned int u32;
#ifdef __64_bit__
typedef long int i64;
typedef unsigned long int u64;
typedef u64 size_t;
#else 
typedef long long int i64;
typedef unsigned long long int u64;
typedef u32 size_t;
#endif
typedef u8 bool;



#endif //SANDBOX_PRIMITIVES_H

