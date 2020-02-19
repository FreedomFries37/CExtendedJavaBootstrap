typedef unsigned long int class_id;
struct class_std_ClassInfo330389123;
struct class_std_ClassInfo330389123* __get_class(class_id);
struct class_std_String823295118;
typedef unsigned char bool;
void* malloc(unsigned int);
void* calloc(unsigned int, unsigned int);
void free(void*);
void print(const char*);
void println(const char*);
void print_s(struct class_std_String823295118*);
void println_s(struct class_std_String823295118*);
static const void* nullptr = 0;
struct class_std_Object691912636;

struct class_std_Object691912636_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals836331605) (void*, struct class_std_Object691912636*);
	void (*drop115478603) (void*);
	struct class_std_String823295118* (*toString1664535608) (void*);
};
struct class_std_Object691912636 {
	struct class_std_Object691912636_vtable* vtable;
	struct class_std_ClassInfo330389123* info;
	long int references;
	struct class_std_ClassInfo330389123* (*getClass2062954782) (void*);
};

struct class_std_ClassInfo330389123* std_Object_getClass2006137327(void*);
int std_Object_hashcode204218920(void*);
bool std_Object_equals779514150(void*, struct class_std_Object691912636*);
void std_Object_drop58661148(void*);
struct class_std_String823295118* std_Object_toString1721353063(void*);

struct class_std_Object691912636* construct_std_Object0_2078748101(void*);

static struct class_std_Object691912636* class_std_Object691912636_init971822767() {
    struct class_std_Object691912636* output;
    output = calloc(1, sizeof(struct class_std_Object691912636));
    struct class_std_Object691912636_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_Object691912636_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode204218920;
    (*vtable).equals836331605 = std_Object_equals779514150;
    (*vtable).drop115478603 = std_Object_drop58661148;
    (*vtable).toString1664535608 = std_Object_toString1721353063;
    (*output).getClass2062954782 = std_Object_getClass2006137327;
    (*output).info = (struct class_std_ClassInfo330389123*) __get_class(0);
    (*output).references = (long int) {0};
    return output;
}



struct class_std_ClassInfo330389123;

struct class_std_ClassInfo330389123_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals836331605) (void*, struct class_std_Object691912636*);
	void (*drop115478603) (void*);
	struct class_std_String823295118* (*toString1664535608) (void*);
	bool (*equals1507374867) (void*, struct class_std_ClassInfo330389123*);
};
struct class_std_ClassInfo330389123 {
	struct class_std_ClassInfo330389123_vtable* vtable;
	struct class_std_ClassInfo330389123* info;
	long int references;
	struct class_std_ClassInfo330389123* (*getClass2062954782) (void*);
	struct class_std_String823295118* name;
	struct class_std_ClassInfo330389123* parent;
	int classHash;
	struct class_std_String823295118* (*getName37078109) (void*);
	bool (*is2131817920) (void*, struct class_std_Object691912636*);
	bool (*is_class1382397577) (void*, struct class_std_ClassInfo330389123*);
};

struct class_std_String823295118* std_ClassInfo_getName1296410126(void*);
bool std_ClassInfo_is903817359(void*, struct class_std_Object691912636*);
bool std_ClassInfo_is_class123065560(void*, struct class_std_ClassInfo330389123*);
int std_Object_hashcode204218920(void*);
bool std_ClassInfo_equals2095663622(void*, struct class_std_Object691912636*);
void std_Object_drop58661148(void*);
struct class_std_String823295118* std_Object_toString1721353063(void*);
bool std_ClassInfo_equals1528260412(void*, struct class_std_ClassInfo330389123*);

struct class_std_ClassInfo330389123* construct_std_ClassInfo0_1541558399(void*);

static struct class_std_ClassInfo330389123* class_std_ClassInfo330389123_init1918608273() {
    struct class_std_ClassInfo330389123* output;
    output = calloc(1, sizeof(struct class_std_ClassInfo330389123));
    struct class_std_ClassInfo330389123_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_ClassInfo330389123_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode204218920;
    (*vtable).equals836331605 = std_ClassInfo_equals2095663622;
    (*vtable).drop115478603 = std_Object_drop58661148;
    (*vtable).toString1664535608 = std_Object_toString1721353063;
    (*vtable).equals1507374867 = std_ClassInfo_equals1528260412;
    (*output).getClass2062954782 = std_Object_getClass2006137327;
    (*output).getName37078109 = std_ClassInfo_getName1296410126;
    (*output).is2131817920 = std_ClassInfo_is903817359;
    (*output).is_class1382397577 = std_ClassInfo_is_class123065560;
    (*output).info = (struct class_std_ClassInfo330389123*) __get_class(1);
    (*output).references = (long int) {0};
    (*output).name = (struct class_std_String823295118*) {0};
    (*output).parent = (struct class_std_ClassInfo330389123*) {0};
    (*output).classHash = (int) {0};
    return output;
}





static // bool super_std_Object_equals779514150 (CXClass std::Object*)
bool super_std_Object_equals7795141501950309224(void* __this, struct class_std_Object691912636* other) {
    struct class_std_ClassInfo330389123* this = __this;
    struct class_std_Object691912636* super = __this;
    bool (*old) (void*, struct class_std_Object691912636*);
    old = (*(*this).vtable).equals836331605;
    (*(*this).vtable).equals836331605 = std_Object_equals779514150;
    unsigned char output;
    output = (*(*this).vtable).equals836331605(__this, other);
    (*(*this).vtable).equals836331605 = old;
    return output;
}

struct class_std_String823295118;

struct class_std_String823295118_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals836331605) (void*, struct class_std_Object691912636*);
	void (*drop115478603) (void*);
	struct class_std_String823295118* (*toString1664535608) (void*);
};
struct class_std_String823295118 {
	struct class_std_String823295118_vtable* vtable;
	struct class_std_ClassInfo330389123* info;
	long int references;
	struct class_std_ClassInfo330389123* (*getClass2062954782) (void*);
	char* backingPtr;
	int length;
	struct class_std_String823295118* (*concat637661050) (void*, struct class_std_String823295118*);
	struct class_std_String823295118* (*concat1663899886) (void*, char*);
	struct class_std_String823295118* (*concat_integer631888426) (void*, long int);
	const char* (*getCStr36737184) (void*);
};

struct class_std_String823295118* std_String_concat785206377(void*, struct class_std_String823295118*);
struct class_std_String823295118* std_String_concat1811445213(void*, char*);
struct class_std_String823295118* std_String_concat_integer484343099(void*, long int);
const char* std_String_getCStr110808143(void*);
int std_Object_hashcode204218920(void*);
bool std_Object_equals779514150(void*, struct class_std_Object691912636*);
void std_String_drop32066724(void*);
struct class_std_String823295118* std_Object_toString1721353063(void*);

struct class_std_String823295118* construct_std_String342477975_2014277902(void*, const char*);
struct class_std_String823295118* construct_std_String0_242852001(void*);

static struct class_std_String823295118* class_std_String823295118_init1887106003() {
    struct class_std_String823295118* output;
    output = calloc(1, sizeof(struct class_std_String823295118));
    struct class_std_String823295118_vtable* vtable;
    vtable = malloc(sizeof(struct class_std_String823295118_vtable));
    (*output).vtable = vtable;
    (*vtable).offset = 0;
    (*vtable).hashcode261036375 = std_Object_hashcode204218920;
    (*vtable).equals836331605 = std_Object_equals779514150;
    (*vtable).drop115478603 = std_String_drop32066724;
    (*vtable).toString1664535608 = std_Object_toString1721353063;
    (*output).getClass2062954782 = std_Object_getClass2006137327;
    (*output).concat637661050 = std_String_concat785206377;
    (*output).concat1663899886 = std_String_concat1811445213;
    (*output).concat_integer631888426 = std_String_concat_integer484343099;
    (*output).getCStr36737184 = std_String_getCStr110808143;
    (*output).info = (struct class_std_ClassInfo330389123*) __get_class(2);
    (*output).references = (long int) {0};
    (*output).backingPtr = (char*) {0};
    (*output).length = (int) {0};
    return output;
}






static // void super_std_Object_drop58661148 ()
void super_std_Object_drop586611481217300209(void* __this) {
    struct class_std_String823295118* this = __this;
    struct class_std_Object691912636* super = __this;
    void (*old) (void*);
    old = (*(*this).vtable).drop115478603;
    (*(*this).vtable).drop115478603 = std_Object_drop58661148;
    (*(*this).vtable).drop115478603(__this);
    (*(*this).vtable).drop115478603 = old;
}

void __init_reflection();
int __main(int, struct class_std_String823295118*[]);
static struct class_std_ClassInfo330389123* class_std_ClassInfo330389123_info = 0;
static struct class_std_ClassInfo330389123* class_std_Object691912636_info = 0;
static struct class_std_ClassInfo330389123* class_std_String823295118_info = 0;
static struct class_std_ClassInfo330389123* class_std_Inner1592750445_info = 0;
struct class_std_ClassInfo330389123* __get_class(class_id id) {
    if ((id==1)) return class_std_ClassInfo330389123_info;
    if ((id==0)) return class_std_Object691912636_info;
    if ((id==2)) return class_std_String823295118_info;
    if ((id==6)) return class_std_Inner1592750445_info;
    return nullptr;
}

int main(int argc, char* argv[]) {
    __init_reflection();
    struct class_std_String823295118* args[argc];
    for (int i = 0;(i<argc); i++)args[i] = construct_std_String342477975_2014277902(class_std_String823295118_init1887106003(), argv[i]);
    int output = __main(argc, args);
    for (int i = 0;(i<argc); i++)(*args[i]).vtable->drop115478603(args[i]);
    return output;
}

void __init_reflection() {
    class_std_ClassInfo330389123_info = construct_std_ClassInfo0_1541558399(class_std_ClassInfo330389123_init1918608273());
    (*class_std_ClassInfo330389123_info).name = construct_std_String342477975_2014277902(class_std_String823295118_init1887106003(), "std::ClassInfo");
    (*class_std_ClassInfo330389123_info).classHash = 1652128221;
    (*class_std_ClassInfo330389123_info).info = class_std_ClassInfo330389123_info;
    class_std_Object691912636_info = construct_std_ClassInfo0_1541558399(class_std_ClassInfo330389123_init1918608273());
    (*class_std_Object691912636_info).name = construct_std_String342477975_2014277902(class_std_String823295118_init1887106003(), "std::Object");
    (*class_std_Object691912636_info).classHash = -25544764;
    class_std_String823295118_info = construct_std_ClassInfo0_1541558399(class_std_ClassInfo330389123_init1918608273());
    (*class_std_String823295118_info).name = construct_std_String342477975_2014277902(class_std_String823295118_init1887106003(), "std::String");
    (*class_std_String823295118_info).classHash = -247655118;
    class_std_Inner1592750445_info = construct_std_ClassInfo0_1541558399(class_std_ClassInfo330389123_init1918608273());
    (*class_std_Inner1592750445_info).name = construct_std_String342477975_2014277902(class_std_String823295118_init1887106003(), "std::Inner");
    (*class_std_Inner1592750445_info).classHash = -2130623539;
    (*class_std_ClassInfo330389123_info).parent = __get_class(0);
    (*class_std_Object691912636_info).parent = nullptr;
    (*class_std_String823295118_info).parent = __get_class(0);
    (*class_std_Inner1592750445_info).parent = __get_class(0);
}

