#ifndef LINKED_LIST_HEADER
#define LINKED_LIST_HEADER

#include "prelude.h"

in ll {
	class Node {
		private void* value_ptr;
		public Node prev;
		public Node next;

		public Node(void* value_ptr) {
        	this->value_ptr = value_ptr;
        }

		public void* get_value_ptr();
		public void set_value_ptr(void* val);
	};

	class IntLinkedList {
		private Node head;
		private Node tail;
		private int size;

		public IntLinkedList() {
			this->head = 0;
			this->tail = 0;
		}

		public void add(int element);
		public int get(int index);
		public int size();
		public bool remove(int value);

		public bool remove_nth_element(int n);

		private Node get_nth_node(int n);
	};
}


bool boolean_test();


#endif