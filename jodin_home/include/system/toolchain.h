/*

This file is meant to be the list of functions that a toolchain must implement in some way for Jodin to run and
work with the operating system


*/

#ifndef __TOOLCHAIN__HEADER__
#define __TOOLCHAIN__HEADER__
in std {
// Process Control

virtual class AbstractProcess {
	private status_t status;
	private String command;
	private String[] args;
	private String[] env;

	public Process(std::String command);
	public Process(std::String command, std::String[] args);
	public Process(std::String command, std::String[] args, std::String[] env);

	virtual public int fork();
	virtual public int await();

	virtual public void wait();
}

void exit(int code);

// File management

virtual class AbstractFile {

	virtual void flush();
	virtual void close();

	virtual char read();
	virtual void write(char c);
	virtual void change_position(usize position);
}


// Device management


// Information management

u64 time();


// Communication management

}

#endif