#ifndef LINKED_LIST_HEADER
#define LINKED_LIST_HEADER

/*

This file represents a linked list!

*/

in ll {
	for <T> class Node {
		private T value_ptr;
		public Node<T> prev;
		public Node<T> next;

		public Node(T value_ptr) {
        	this->value_ptr = value_ptr;
        }

		public T get_value_ptr();
		public void set_value_ptr(T val);
	};

	for <T> class LinkedList {
		private Node<T> head;
		private Node<T> tail;
		private int size;

		public LinkedList() {
			this->head = nullptr;
			this->tail = nullptr;
		}

		public void add(T element);
		public T get(int index);
		public int size();
		public bool remove(int value);

		public bool remove_nth_element(int n);

		private Node<T> get_nth_node(int n);
	};
}


bool boolean_test();


#endif