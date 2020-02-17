

struct class_std_ClassInfo1072900803;
struct class_std_String1565806798;
typedef unsigned long int class_id;
void* malloc(unsigned int);
void* calloc(unsigned int, unsigned int);
void free(void*);
void print(const char*);
void println(const char*);
void print_s(struct class_std_String1565806798*);
void println_s(struct class_std_String1565806798*);
struct class_std_ClassInfo1072900803* getClass(class_id);
struct class_std_Object1434424316;

struct class_std_Object1434424316_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals665279894) (void*, struct class_std_Object1434424316*);
	void (*drop115478603) (void*);
	struct class_std_String1565806798* (*toString1664535608) (void*);
};
struct class_std_Object1434424316 {
	struct class_std_Object1434424316_vtable* vtable;
	struct class_std_ClassInfo1072900803* info;
	long int references;
	struct class_std_ClassInfo1072900803* (*getClass2062954782) (void*);
};

struct class_std_ClassInfo1072900803* std_Object_getClass1590701935(void*);
int std_Object_hashcode902346954(void*);
int std_Object_equals1306590473(void*, struct class_std_Object1434424316*);
void std_Object_drop756789182(void*);
struct class_std_String1565806798* std_Object_toString1023225029(void*);

struct class_std_Object1434424316* construct_std_Object1_1371321660(void*);

static struct class_std_Object1434424316* class_std_Object1434424316_init1258967178() {
    struct class_std_Object1434424316* output;
    output = calloc(1, sizeof(struct class_std_Object1434424316));
    struct class_std_Object1434424316_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_Object1434424316_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode902346954;
    (*vtable).equals665279894 = std_Object_equals1306590473;
    (*vtable).drop115478603 = std_Object_drop756789182;
    (*vtable).toString1664535608 = std_Object_toString1023225029;
    (*output).getClass2062954782 = std_Object_getClass1590701935;
    (*output).info = (struct class_std_ClassInfo1072900803*) {0};
    (*output).references = (long int) {0};
    return output;
}



struct class_std_ClassInfo1072900803;

struct class_std_ClassInfo1072900803_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals665279894) (void*, struct class_std_Object1434424316*);
	void (*drop115478603) (void*);
	struct class_std_String1565806798* (*toString1664535608) (void*);
	int (*equals1298758559) (void*, struct class_std_ClassInfo1072900803*);
};
struct class_std_ClassInfo1072900803 {
	struct class_std_ClassInfo1072900803_vtable* vtable;
	struct class_std_ClassInfo1072900803* info;
	long int references;
	struct class_std_ClassInfo1072900803* (*getClass2062954782) (void*);
	struct class_std_String1565806798* name;
	struct class_std_ClassInfo1072900803* parent;
	int classHash;
	struct class_std_String1565806798* (*getName37078109) (void*);
	int (*is1960766209) (void*, struct class_std_Object1434424316*);
};

struct class_std_String1565806798* std_ClassInfo_getName1994538160(void*);
int std_ClassInfo_is376741036(void*, struct class_std_Object1434424316*);
int std_Object_hashcode902346954(void*);
int std_ClassInfo_equals1672227351(void*, struct class_std_Object1434424316*);
void std_Object_drop756789182(void*);
struct class_std_String1565806798* std_Object_toString1023225029(void*);
int std_ClassInfo_equals658701492(void*, struct class_std_ClassInfo1072900803*);

struct class_std_ClassInfo1072900803* construct_std_ClassInfo226964094_267255142(void*, struct class_std_String1565806798*, struct class_std_ClassInfo1072900803*, int);

static struct class_std_ClassInfo1072900803* class_std_ClassInfo1072900803_init767881601() {
    struct class_std_ClassInfo1072900803* output;
    output = calloc(1, sizeof(struct class_std_ClassInfo1072900803));
    struct class_std_ClassInfo1072900803_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_ClassInfo1072900803_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode902346954;
    (*vtable).equals665279894 = std_ClassInfo_equals1672227351;
    (*vtable).drop115478603 = std_Object_drop756789182;
    (*vtable).toString1664535608 = std_Object_toString1023225029;
    (*vtable).equals1298758559 = std_ClassInfo_equals658701492;
    (*output).getClass2062954782 = std_Object_getClass1590701935;
    (*output).getName37078109 = std_ClassInfo_getName1994538160;
    (*output).is1960766209 = std_ClassInfo_is376741036;
    (*output).info = output;
    (*output).references = (long int) {0};
    (*output).name = (struct class_std_String1565806798*) {0};
    (*output).parent = (struct class_std_ClassInfo1072900803*) {0};
    (*output).classHash = (int) {0};
    return output;
}




static int super_std_Object_equals1306590473343211960(void* __this, struct class_std_Object1434424316* other) {
    struct class_std_ClassInfo1072900803* this = __this;
    struct class_std_Object1434424316* super = __this;
    int (*old) (void*, struct class_std_Object1434424316*);
    old = (*(*this).vtable).equals665279894;
    (*(*this).vtable).equals665279894 = std_Object_equals1306590473;
    int output;
    output = (*(*this).vtable).equals665279894(__this, other);
    (*(*this).vtable).equals665279894 = old;
    return output;
}

struct class_std_String1565806798;

struct class_std_String1565806798_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals665279894) (void*, struct class_std_Object1434424316*);
	void (*drop115478603) (void*);
	struct class_std_String1565806798* (*toString1664535608) (void*);
};
struct class_std_String1565806798 {
	struct class_std_String1565806798_vtable* vtable;
	struct class_std_ClassInfo1072900803* info;
	long int references;
	struct class_std_ClassInfo1072900803* (*getClass2062954782) (void*);
	char* backingPtr;
	int length;
	struct class_std_String1565806798* (*concat1580926564) (void*, struct class_std_String1565806798*);
	struct class_std_String1565806798* (*concat1663899886) (void*, char*);
	const char* (*getCStr36737184) (void*);
};

struct class_std_String1565806798* std_String_concat2131509271(void*, struct class_std_String1565806798*);
struct class_std_String1565806798* std_String_concat1113317179(void*, char*);
const char* std_String_getCStr587319891(void*);
int std_Object_hashcode902346954(void*);
int std_Object_equals1306590473(void*, struct class_std_Object1434424316*);
void std_String_drop666061310(void*);
struct class_std_String1565806798* std_Object_toString1023225029(void*);

struct class_std_String1565806798* construct_std_String1983747951_587420416(void*, const char*);
struct class_std_String1565806798* construct_std_String1_1087749536(void*);

static struct class_std_String1565806798* class_std_String1565806798_init1174076527() {
    struct class_std_String1565806798* output;
    output = calloc(1, sizeof(struct class_std_String1565806798));
    struct class_std_String1565806798_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_String1565806798_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode902346954;
    (*vtable).equals665279894 = std_Object_equals1306590473;
    (*vtable).drop115478603 = std_String_drop666061310;
    (*vtable).toString1664535608 = std_Object_toString1023225029;
    (*output).getClass2062954782 = std_Object_getClass1590701935;
    (*output).concat1580926564 = std_String_concat2131509271;
    (*output).concat1663899886 = std_String_concat1113317179;
    (*output).getCStr36737184 = std_String_getCStr587319891;
    (*output).info = (struct class_std_ClassInfo1072900803*) {0};
    (*output).references = (long int) {0};
    (*output).backingPtr = (char*) {0};
    (*output).length = (int) {0};
    return output;
}





static void super_std_Object_drop756789182233676185(void* __this) {
    struct class_std_String1565806798* this = __this;
    struct class_std_Object1434424316* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).drop115478603;
    (*(*this).vtable).drop115478603 = std_Object_drop756789182;
    (*(*this).vtable).drop115478603(__this);
    (*(*this).vtable).drop115478603 = old;
}

struct class_shape2093772692;

struct class_shape2093772692_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals665279894) (void*, struct class_std_Object1434424316*);
	void (*drop115478603) (void*);
	struct class_std_String1565806798* (*toString1664535608) (void*);
	double (*get_area2088501554) (void*);
	int (*get_num_sides2091034698) (void*);
};
struct class_shape2093772692 {
	struct class_shape2093772692_vtable* vtable;
	struct class_std_ClassInfo1072900803* info;
	long int references;
	struct class_std_ClassInfo1072900803* (*getClass2062954782) (void*);
};

int std_Object_hashcode902346954(void*);
int std_Object_equals1306590473(void*, struct class_std_Object1434424316*);
void std_Object_drop756789182(void*);
struct class_std_String1565806798* std_Object_toString1023225029(void*);
double shape_get_area1940843611(void*);
int shape_get_num_sides1825412567(void*);


static struct class_shape2093772692* class_shape2093772692_init1190351855() {
    struct class_shape2093772692* output;
    output = calloc(1, sizeof(struct class_shape2093772692));
    struct class_shape2093772692_vtable* vtable;
    vtable = malloc(sizeof(struct class_shape2093772692_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode902346954;
    (*vtable).equals665279894 = std_Object_equals1306590473;
    (*vtable).drop115478603 = std_Object_drop756789182;
    (*vtable).toString1664535608 = std_Object_toString1023225029;
    (*vtable).get_area2088501554 = shape_get_area1940843611;
    (*vtable).get_num_sides2091034698 = shape_get_num_sides1825412567;
    (*output).getClass2062954782 = std_Object_getClass1590701935;
    (*output).info = (struct class_std_ClassInfo1072900803*) {0};
    (*output).references = (long int) {0};
    return output;
}


double shape_get_area1940843611(void* __this) {
    struct class_shape2093772692* this = __this;
    struct class_std_Object1434424316* super = __this;
    return 0.0;
}


int shape_get_num_sides1825412567(void* __this) {
    struct class_shape2093772692* this = __this;
    struct class_std_Object1434424316* super = __this;
    return 0.0;
}


struct class_rectangle1189294750;

struct class_rectangle1189294750_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals665279894) (void*, struct class_std_Object1434424316*);
	void (*drop115478603) (void*);
	struct class_std_String1565806798* (*toString1664535608) (void*);
	double (*get_area2088501554) (void*);
	int (*get_num_sides2091034698) (void*);
	double (*get_side5379507) (void*, int);
};
struct class_rectangle1189294750 {
	struct class_rectangle1189294750_vtable* vtable;
	struct class_std_ClassInfo1072900803* info;
	long int references;
	struct class_std_ClassInfo1072900803* (*getClass2062954782) (void*);
	double s1;
	double s2;
};

int std_Object_hashcode902346954(void*);
int std_Object_equals1306590473(void*, struct class_std_Object1434424316*);
void std_Object_drop756789182(void*);
struct class_std_String1565806798* std_Object_toString1023225029(void*);
double rectangle_get_area375180645(void*);
int rectangle_get_num_sides490611689(void*);
double rectangle_get_side1707941402(void*, int);

struct class_rectangle1189294750* construct_rectangle908211569_1246851790(void*, double, double);

static struct class_rectangle1189294750* class_rectangle1189294750_init223305488() {
    struct class_rectangle1189294750* output;
    output = calloc(1, sizeof(struct class_rectangle1189294750));
    struct class_rectangle1189294750_vtable* vtable;
    vtable = malloc(sizeof(struct class_rectangle1189294750_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode902346954;
    (*vtable).equals665279894 = std_Object_equals1306590473;
    (*vtable).drop115478603 = std_Object_drop756789182;
    (*vtable).toString1664535608 = std_Object_toString1023225029;
    (*vtable).get_area2088501554 = rectangle_get_area375180645;
    (*vtable).get_num_sides2091034698 = rectangle_get_num_sides490611689;
    (*vtable).get_side5379507 = rectangle_get_side1707941402;
    (*output).getClass2062954782 = std_Object_getClass1590701935;
    (*output).info = (struct class_std_ClassInfo1072900803*) {0};
    (*output).references = (long int) {0};
    (*output).s1 = (double) {0};
    (*output).s2 = (double) {0};
    return output;
}



struct class_rectangle1189294750* construct_rectangle908211569_1246851790(void* __this, double s1, double s2) {
    struct class_rectangle1189294750* this = (struct class_rectangle1189294750*) __this;
    (*this).s1 = s1;
    (*this).s2 = s2;
    return this;
}

static double super_shape_get_area19408436111411008244(void* __this) {
    struct class_rectangle1189294750* this = __this;
    struct class_shape2093772692* super = __this;
    double (*old) (void*);
    old = (*(*this).vtable).get_area2088501554;
    (*(*this).vtable).get_area2088501554 = shape_get_area1940843611;
    double output;
    output = (*(*this).vtable).get_area2088501554(__this);
    (*(*this).vtable).get_area2088501554 = old;
    return output;
}

static int super_shape_get_num_sides182541256792697492(void* __this) {
    struct class_rectangle1189294750* this = __this;
    struct class_shape2093772692* super = __this;
    int (*old) (void*);
    old = (*(*this).vtable).get_num_sides2091034698;
    (*(*this).vtable).get_num_sides2091034698 = shape_get_num_sides1825412567;
    int output;
    output = (*(*this).vtable).get_num_sides2091034698(__this);
    (*(*this).vtable).get_num_sides2091034698 = old;
    return output;
}

double rectangle_get_area375180645(void* __this) {
    struct class_rectangle1189294750* this = __this;
    struct class_shape2093772692* super = __this;
    return ((*this).s1*(*this).s2);
}


int rectangle_get_num_sides490611689(void* __this) {
    struct class_rectangle1189294750* this = __this;
    struct class_shape2093772692* super = __this;
    return 4;
}


double rectangle_get_side1707941402(void* __this, int i) {
    struct class_rectangle1189294750* this = __this;
    struct class_shape2093772692* super = __this;
    if ((i==0)) return (*this).s1; else if ((i==1)) return (*this).s2;

    return -1;
}


struct class_square1089698064;

struct class_square1089698064_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	int (*equals665279894) (void*, struct class_std_Object1434424316*);
	void (*drop115478603) (void*);
	struct class_std_String1565806798* (*toString1664535608) (void*);
	double (*get_area2088501554) (void*);
	int (*get_num_sides2091034698) (void*);
	double (*get_side5379507) (void*, int);
};
struct class_square1089698064 {
	struct class_square1089698064_vtable* vtable;
	struct class_std_ClassInfo1072900803* info;
	long int references;
	struct class_std_ClassInfo1072900803* (*getClass2062954782) (void*);
	double s1;
	double s2;
};

int std_Object_hashcode902346954(void*);
int std_Object_equals1306590473(void*, struct class_std_Object1434424316*);
void std_Object_drop756789182(void*);
struct class_std_String1565806798* std_Object_toString1023225029(void*);
double rectangle_get_area375180645(void*);
int square_get_num_sides403937705(void*);
double square_get_side1794615386(void*, int);

struct class_square1089698064* construct_square985934133_1422004039(void*, double);

static struct class_square1089698064* class_square1089698064_init239779263() {
    struct class_square1089698064* output;
    output = calloc(1, sizeof(struct class_square1089698064));
    struct class_square1089698064_vtable* vtable;
    vtable = malloc(sizeof(struct class_square1089698064_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode902346954;
    (*vtable).equals665279894 = std_Object_equals1306590473;
    (*vtable).drop115478603 = std_Object_drop756789182;
    (*vtable).toString1664535608 = std_Object_toString1023225029;
    (*vtable).get_area2088501554 = rectangle_get_area375180645;
    (*vtable).get_num_sides2091034698 = square_get_num_sides403937705;
    (*vtable).get_side5379507 = square_get_side1794615386;
    (*output).getClass2062954782 = std_Object_getClass1590701935;
    (*output).info = (struct class_std_ClassInfo1072900803*) {0};
    (*output).references = (long int) {0};
    (*output).s1 = (double) {0};
    (*output).s2 = (double) {0};
    return output;
}



struct class_square1089698064* construct_square985934133_1422004039(void* __this, double s) {
    construct_rectangle908211569_1246851790(__this, s, s);
    struct class_square1089698064* this = (struct class_square1089698064*) __this;
    return this;
}

static double super_rectangle_get_side1707941402105495479(void* __this, int i) {
    struct class_square1089698064* this = __this;
    struct class_rectangle1189294750* super = __this;
    double (*old) (void*, int);
    old = (*(*this).vtable).get_side5379507;
    (*(*this).vtable).get_side5379507 = rectangle_get_side1707941402;
    double output;
    output = (*(*this).vtable).get_side5379507(__this, i);
    (*(*this).vtable).get_side5379507 = old;
    return output;
}

static int super_rectangle_get_num_sides4906116891811574149(void* __this) {
    struct class_square1089698064* this = __this;
    struct class_rectangle1189294750* super = __this;
    int (*old) (void*);
    old = (*(*this).vtable).get_num_sides2091034698;
    (*(*this).vtable).get_num_sides2091034698 = rectangle_get_num_sides490611689;
    int output;
    output = (*(*this).vtable).get_num_sides2091034698(__this);
    (*(*this).vtable).get_num_sides2091034698 = old;
    return output;
}

int square_get_num_sides403937705(void* __this) {
    struct class_square1089698064* this = __this;
    struct class_rectangle1189294750* super = __this;
    return super_rectangle_get_num_sides4906116891811574149(super);
}


double square_get_side1794615386(void* __this, int i) {
    struct class_square1089698064* this = __this;
    struct class_rectangle1189294750* super = __this;
    double s = (*this).s1;
    return super_rectangle_get_side1707941402105495479(super, 1);
}


void increment(int* n) {
    (*n) = ((*n)+1);
    return;
}

int returnValue() {
    do {
        if (0) {
            return 1;
        }



        if (1) ; else for (;; );
    }while (1);







    return 0;
}

int main() {
    struct class_rectangle1189294750* r1 = construct_square985934133_1422004039(class_square1089698064_init239779263(), 5.0);

    double area = (*r1).vtable->get_area2088501554(r1);


    return 0;
}

