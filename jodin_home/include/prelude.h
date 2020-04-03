#ifndef __PRELUDE_HEADER__
#define __PRELUDE_HEADER__

#include <reflection.h>
#include <primitives.h>


in std class String;
in std class ClassInfo;
in std class Int;

void* calloc(size_t size, size_t length);


void free(void* ptr);
void exit(int status);
void panic(std::String msg);

void print(const char* c);
void println(const char* c);
void print_s(std::String o);
void println_s(std::String o);

const void* nullptr = (void*) 0;



in std {
	// ClassInfo get_class(class_id id);s

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

		virtual public void print();
		public void println();


	};



	class ClassInfo {


		private String name;
		private ClassInfo parent;
		private i32 classHash;

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
		int cLength;

		public String(const char* bp);
		public String(char c);
		public String();
		public String(String other);

		virtual public void drop();

		public String concat(String other);

		public String concat(char* other);

		public String concat_integer(long other);

		public String concat(Object other);

		public String concat(char c);

		public const char* getCStr();

		virtual public int length();

		virtual public String toString();
    };




}



#endif
