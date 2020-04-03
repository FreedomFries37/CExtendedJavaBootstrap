#ifndef __STDIO__HEADER__
#define __STDIO__HEADER__

#include <primitives.h>
#include <system/filesystem.h>

typedef std::File FILE;
typedef u64 fpos_t;

#define NULL nullptr;
#define EOF -1;

#define stdin (access_fd(0))
#define stdout (access_fd(1))
#define stderr (access_fd(2))

int fclose(FILE stream);
int clearerr(FILE stream);
int feof(FILE stream);
int ferror(FILE stream);
int fflush(FILE stream);

int fgetpos(FILE stream, fpos_t* pos);
FILE fopen(const char* filename, const char* mode);

in std
class FileWriter {
	private FILE f;

	public FileWriter(FILE f);

	public void writeln();

	public void write(char c);
	public void writeln(char c);

	public void write(std::String s);
	public void writeln(std::String s);

	public void write(std::Object o);
	public void writeln(std::Object o);

	public void flush();
	public void close();
};


#endif