typedef unsigned long int class_id;
struct class_std_ClassInfo612126077;
struct class_std_ClassInfo612126077* __get_class(class_id);
struct class_std_String119220082;
typedef unsigned char bool;
void* malloc(unsigned int);
void* calloc(unsigned int, unsigned int);
void free(void*);
void exit(int);
void panic(struct class_std_String119220082*);
void print(const char*);
void println(const char*);
void print_s(struct class_std_String119220082*);
void println_s(struct class_std_String119220082*);
static const void* nullptr = 0;
struct class_std_Object250602564;

struct class_std_Object250602564_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals123258682) (void*, struct class_std_Object250602564*);
	void (*drop115478603) (void*);
	struct class_std_String119220082* (*toString1664535608) (void*);
};
struct class_std_Object250602564 {
	struct class_std_Object250602564_vtable* __vtable;
	struct class_std_ClassInfo612126077* info;
	long int references;
	struct class_std_ClassInfo612126077* (*getClass2062954782) (void*);
};

struct class_std_ClassInfo612126077* std_Object_getClass937603889(void*);
int std_Object_hashcode1555445000(void*);
bool std_Object_equals1417667307(void*, struct class_std_Object250602564*);
void std_Object_drop1409887228(void*);
struct class_std_String119220082* std_Object_toString370126983(void*);

struct class_std_Object250602564* construct_std_Object0_1574321893(void*);

static struct class_std_Object250602564* class_std_Object250602564_init1401757846() {
    struct class_std_Object250602564* output;
    output = calloc(1, sizeof(struct class_std_Object250602564));
    struct class_std_Object250602564_vtable* __vtable;
    __vtable = malloc(sizeof(struct class_std_Object250602564_vtable));
    (*output).__vtable = __vtable;
    (*__vtable).offset = 0;
    (*__vtable).hashcode261036375 = std_Object_hashcode1555445000;
    (*__vtable).equals123258682 = std_Object_equals1417667307;
    (*__vtable).drop115478603 = std_Object_drop1409887228;
    (*__vtable).toString1664535608 = std_Object_toString370126983;
    (*output).getClass2062954782 = std_Object_getClass937603889;
    (*output).info = (struct class_std_ClassInfo612126077*) __get_class(0);
    (*output).references = (long int) {0};
    return output;
}



struct class_std_ClassInfo612126077;

struct class_std_ClassInfo612126077_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals123258682) (void*, struct class_std_Object250602564*);
	void (*drop115478603) (void*);
	struct class_std_String119220082* (*toString1664535608) (void*);
	bool (*equals1773818959) (void*, struct class_std_ClassInfo612126077*);
};
struct class_std_ClassInfo612126077 {
	struct class_std_ClassInfo612126077_vtable* __vtable;
	struct class_std_ClassInfo612126077* info;
	long int references;
	struct class_std_ClassInfo612126077* (*getClass2062954782) (void*);
	struct class_std_String119220082* name;
	struct class_std_ClassInfo612126077* parent;
	int classHash;
	struct class_std_String119220082* (*getName37078109) (void*);
	bool (*is_object859412335) (void*, struct class_std_Object250602564*);
	bool (*is_class1115953485) (void*, struct class_std_ClassInfo612126077*);
};

struct class_std_String119220082* std_ClassInfo_getName1647331090(void*);
bool std_ClassInfo_is_object824996864(void*, struct class_std_Object250602564*);
bool std_ClassInfo_is_class1494604612(void*, struct class_std_ClassInfo612126077*);
int std_Object_hashcode1555445000(void*);
bool std_ClassInfo_equals1561150517(void*, struct class_std_Object250602564*);
void std_Object_drop1409887228(void*);
struct class_std_String119220082* std_Object_toString370126983(void*);
bool std_ClassInfo_equals89409760(void*, struct class_std_ClassInfo612126077*);

struct class_std_ClassInfo612126077* construct_std_ClassInfo0_1037132191(void*);

static struct class_std_ClassInfo612126077* class_std_ClassInfo612126077_init1481452115() {
    struct class_std_ClassInfo612126077* output;
    output = calloc(1, sizeof(struct class_std_ClassInfo612126077));
    struct class_std_ClassInfo612126077_vtable* __vtable;
    __vtable = malloc(sizeof(struct class_std_ClassInfo612126077_vtable));
    (*output).__vtable = __vtable;
    (*__vtable).offset = 0;
    (*__vtable).hashcode261036375 = std_Object_hashcode1555445000;
    (*__vtable).equals123258682 = std_ClassInfo_equals1561150517;
    (*__vtable).drop115478603 = std_Object_drop1409887228;
    (*__vtable).toString1664535608 = std_Object_toString370126983;
    (*__vtable).equals1773818959 = std_ClassInfo_equals89409760;
    (*output).getClass2062954782 = std_Object_getClass937603889;
    (*output).getName37078109 = std_ClassInfo_getName1647331090;
    (*output).is_object859412335 = std_ClassInfo_is_object824996864;
    (*output).is_class1115953485 = std_ClassInfo_is_class1494604612;
    (*output).info = (struct class_std_ClassInfo612126077*) __get_class(1);
    (*output).references = (long int) {0};
    (*output).name = (struct class_std_String119220082*) {0};
    (*output).parent = (struct class_std_ClassInfo612126077*) {0};
    (*output).classHash = (int) {0};
    return output;
}





static // bool super_std_Object_equals1417667307 (std::Object*)
bool super_std_Object_equals14176673071999440026(void* __this, struct class_std_Object250602564* other) {
    struct class_std_ClassInfo612126077* this = __this;
    struct class_std_Object250602564* super = __this;
    bool (*old) (void*, struct class_std_Object250602564*);
    old = (*(*this).__vtable).equals123258682;
    (*(*this).__vtable).equals123258682 = std_Object_equals1417667307;
    unsigned char output;
    output = (*(*this).__vtable).equals123258682(__this, other);
    (*(*this).__vtable).equals123258682 = old;
    return output;
}

struct class_std_String119220082;

struct class_std_String119220082_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals123258682) (void*, struct class_std_Object250602564*);
	void (*drop115478603) (void*);
	struct class_std_String119220082* (*toString1664535608) (void*);
};
struct class_std_String119220082 {
	struct class_std_String119220082_vtable* __vtable;
	struct class_std_ClassInfo612126077* info;
	long int references;
	struct class_std_ClassInfo612126077* (*getClass2062954782) (void*);
	char* backingPtr;
	int length;
	struct class_std_String119220082* (*concat87696842) (void*, struct class_std_String119220082*);
	struct class_std_String119220082* (*concat1663899886) (void*, char*);
	struct class_std_String119220082* (*concat_integer631888426) (void*, long int);
	const char* (*getCStr36737184) (void*);
};

struct class_std_String119220082* std_String_concat1115983911(void*, struct class_std_String119220082*);
struct class_std_String119220082* std_String_concat460219133(void*, char*);
struct class_std_String119220082* std_String_concat_integer1835569179(void*, long int);
const char* std_String_getCStr1240417937(void*);
int std_Object_hashcode1555445000(void*);
bool std_Object_equals1417667307(void*, struct class_std_Object250602564*);
void std_String_drop1319159356(void*);
struct class_std_String119220082* std_Object_toString370126983(void*);

struct class_std_String119220082* construct_std_String342477975_1509851694(void*, const char*);
struct class_std_String119220082* construct_std_String0_261574207(void*);

static struct class_std_String119220082* class_std_String119220082_init1697583293() {
    struct class_std_String119220082* output;
    output = calloc(1, sizeof(struct class_std_String119220082));
    struct class_std_String119220082_vtable* __vtable;
    __vtable = malloc(sizeof(struct class_std_String119220082_vtable));
    (*output).__vtable = __vtable;
    (*__vtable).offset = 0;
    (*__vtable).hashcode261036375 = std_Object_hashcode1555445000;
    (*__vtable).equals123258682 = std_Object_equals1417667307;
    (*__vtable).drop115478603 = std_String_drop1319159356;
    (*__vtable).toString1664535608 = std_Object_toString370126983;
    (*output).getClass2062954782 = std_Object_getClass937603889;
    (*output).concat87696842 = std_String_concat1115983911;
    (*output).concat1663899886 = std_String_concat460219133;
    (*output).concat_integer631888426 = std_String_concat_integer1835569179;
    (*output).getCStr36737184 = std_String_getCStr1240417937;
    (*output).info = (struct class_std_ClassInfo612126077*) __get_class(2);
    (*output).references = (long int) {0};
    (*output).backingPtr = (char*) {0};
    (*output).length = (int) {0};
    return output;
}






static // void super_std_Object_drop1409887228 ()
void super_std_Object_drop1409887228394420759(void* __this) {
    struct class_std_String119220082* this = __this;
    struct class_std_Object250602564* super = __this;
    void (*old) (void*);
    old = (*(*this).__vtable).drop115478603;
    (*(*this).__vtable).drop115478603 = std_Object_drop1409887228;
    (*(*this).__vtable).drop115478603(__this);
    (*(*this).__vtable).drop115478603 = old;
}

struct class_ll_Node1687898818;

struct class_ll_Node1687898818_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals123258682) (void*, struct class_std_Object250602564*);
	void (*drop115478603) (void*);
	struct class_std_String119220082* (*toString1664535608) (void*);
};
struct class_ll_Node1687898818 {
	struct class_ll_Node1687898818_vtable* __vtable;
	struct class_std_ClassInfo612126077* info;
	long int references;
	struct class_std_ClassInfo612126077* (*getClass2062954782) (void*);
	void* value_ptr;
	struct class_ll_Node1687898818* prev;
	struct class_ll_Node1687898818* next;
	void* (*get_value_ptr138147117) (void*);
	void (*set_value_ptr232255747) (void*, void*);
};

void* ll_Node_get_value_ptr1236203708(void*);
void ll_Node_set_value_ptr865800844(void*, void*);
int std_Object_hashcode1555445000(void*);
bool std_Object_equals1417667307(void*, struct class_std_Object250602564*);
void std_Object_drop1409887228(void*);
struct class_std_String119220082* std_Object_toString370126983(void*);

struct class_ll_Node1687898818* construct_ll_Node112386326_1435033459(void*, void*);

static struct class_ll_Node1687898818* class_ll_Node1687898818_init240947574() {
    struct class_ll_Node1687898818* output;
    output = calloc(1, sizeof(struct class_ll_Node1687898818));
    struct class_ll_Node1687898818_vtable* __vtable;
    __vtable = malloc(sizeof(struct class_ll_Node1687898818_vtable));
    (*output).__vtable = __vtable;
    (*__vtable).offset = 0;
    (*__vtable).hashcode261036375 = std_Object_hashcode1555445000;
    (*__vtable).equals123258682 = std_Object_equals1417667307;
    (*__vtable).drop115478603 = std_Object_drop1409887228;
    (*__vtable).toString1664535608 = std_Object_toString370126983;
    (*output).getClass2062954782 = std_Object_getClass937603889;
    (*output).get_value_ptr138147117 = ll_Node_get_value_ptr1236203708;
    (*output).set_value_ptr232255747 = ll_Node_set_value_ptr865800844;
    (*output).info = (struct class_std_ClassInfo612126077*) __get_class(6);
    (*output).references = (long int) {0};
    (*output).value_ptr = (void*) {0};
    (*output).prev = (struct class_ll_Node1687898818*) {0};
    (*output).next = (struct class_ll_Node1687898818*) {0};
    return output;
}





struct class_ll_Node1687898818* construct_ll_Node112386326_1435033459(void* __this, void* value_ptr) {
    struct class_ll_Node1687898818* this = (struct class_ll_Node1687898818*) __this;
    (*this).value_ptr = value_ptr;
    return this;
}

struct class_ll_IntLinkedList190643162;

struct class_ll_IntLinkedList190643162_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals123258682) (void*, struct class_std_Object250602564*);
	void (*drop115478603) (void*);
	struct class_std_String119220082* (*toString1664535608) (void*);
};
struct class_ll_IntLinkedList190643162 {
	struct class_ll_IntLinkedList190643162_vtable* __vtable;
	struct class_std_ClassInfo612126077* info;
	long int references;
	struct class_std_ClassInfo612126077* (*getClass2062954782) (void*);
	struct class_ll_Node1687898818* head;
	struct class_ll_Node1687898818* tail;
	int size;
	void (*add1971166796) (void*, int);
	int (*get1971160983) (void*, int);
	int (*size115917149) (void*);
	bool (*remove1389093271) (void*, int);
	bool (*remove_nth_element157255927) (void*, int);
	struct class_ll_Node1687898818* (*get_nth_node1776022683) (void*, int);
};

void ll_IntLinkedList_add1241942693(void*, int);
int ll_IntLinkedList_get1241948506(void*, int);
int ll_IntLinkedList_size965940658(void*);
bool ll_IntLinkedList_remove307235464(void*, int);
bool ll_IntLinkedList_remove_nth_element924601880(void*, int);
struct class_ll_Node1687898818* ll_IntLinkedList_get_nth_node694164876(void*, int);
int std_Object_hashcode1555445000(void*);
bool std_Object_equals1417667307(void*, struct class_std_Object250602564*);
void std_Object_drop1409887228(void*);
struct class_std_String119220082* std_Object_toString370126983(void*);

struct class_ll_IntLinkedList190643162* construct_ll_IntLinkedList0_233050769(void*);

static struct class_ll_IntLinkedList190643162* class_ll_IntLinkedList190643162_init1930945654() {
    struct class_ll_IntLinkedList190643162* output;
    output = calloc(1, sizeof(struct class_ll_IntLinkedList190643162));
    struct class_ll_IntLinkedList190643162_vtable* __vtable;
    __vtable = malloc(sizeof(struct class_ll_IntLinkedList190643162_vtable));
    (*output).__vtable = __vtable;
    (*__vtable).offset = 0;
    (*__vtable).hashcode261036375 = std_Object_hashcode1555445000;
    (*__vtable).equals123258682 = std_Object_equals1417667307;
    (*__vtable).drop115478603 = std_Object_drop1409887228;
    (*__vtable).toString1664535608 = std_Object_toString370126983;
    (*output).getClass2062954782 = std_Object_getClass937603889;
    (*output).add1971166796 = ll_IntLinkedList_add1241942693;
    (*output).get1971160983 = ll_IntLinkedList_get1241948506;
    (*output).size115917149 = ll_IntLinkedList_size965940658;
    (*output).remove1389093271 = ll_IntLinkedList_remove307235464;
    (*output).remove_nth_element157255927 = ll_IntLinkedList_remove_nth_element924601880;
    (*output).get_nth_node1776022683 = ll_IntLinkedList_get_nth_node694164876;
    (*output).info = (struct class_std_ClassInfo612126077*) __get_class(7);
    (*output).references = (long int) {0};
    (*output).head = (struct class_ll_Node1687898818*) {0};
    (*output).tail = (struct class_ll_Node1687898818*) {0};
    (*output).size = (int) {0};
    return output;
}









struct class_ll_IntLinkedList190643162* construct_ll_IntLinkedList0_233050769(void* __this) {
    struct class_ll_IntLinkedList190643162* this = (struct class_ll_IntLinkedList190643162*) __this;
    (*this).head = 0;
    (*this).tail = 0;
    return this;
}

bool boolean_test();
int __main(int argc, struct class_std_String119220082* argv[]) {
    unsigned char val = boolean_test();











    for (int i = 0;(i<argc); i++){
        println_s(argv[i]);
    }


























    return val;
}

