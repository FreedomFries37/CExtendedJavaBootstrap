#ifndef __PRELUDE_HEADER__
#define __PRELUDE_HEADER__

#include <reflection.h>


in std class String;

typedef unsigned char bool;



void* malloc(unsigned int sz);
void* calloc(unsigned int num, unsigned int sz);
void free(void* ptr);
void exit(int status);
void panic(std::String msg);

void print(const char* c);
void println(const char* c);
void print_s(std::String o);
void println_s(std::String o);

const void* nullptr = 0;


in std {
	// ClassInfo get_class(class_id id);

	[setAsDefaultInheritance]
	class Object { // default inheritence is initially null
		private ClassInfo info;
		public long references; // if using garbage collection

		public Object();

		virtual public int hashcode();
		virtual public bool equals(Object other);
		virtual public void drop();



		virtual public String toString();

		public ClassInfo getClass();


	};

	class ClassInfo {


		private String name;
		private ClassInfo parent;
		private int classHash;

		public ClassInfo();

		public String getName();

		public bool is_object(Object o);
		private bool is_class(ClassInfo o);

		virtual public bool equals(Object other);
		virtual public bool equals(ClassInfo other);
	};

	class String{
		char* backingPtr;
		int length;

		public String(const char* bp);

		public String();

		virtual public void drop();

		public String concat(String other);

		public String concat(char* other);

		public String concat_integer(long other);

		public const char* getCStr();
    };

}

#endif
