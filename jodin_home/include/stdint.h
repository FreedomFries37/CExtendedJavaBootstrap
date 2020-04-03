#ifndef __STDINT_HEADER__
#define __STDINT_HEADER__

in std {

	class Int {
		private int value;

		public Int(int value);

		public int getValue();

		public void setValue(int value);

		virtual public std::String toString();

		public Int abs();

		public Int pow(int exp);
	};

}

#endif