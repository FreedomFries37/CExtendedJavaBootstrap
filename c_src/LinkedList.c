typedef unsigned long int class_id;
struct class_std_ClassInfo215860611;
struct class_std_ClassInfo215860611* __get_class(class_id);
struct class_std_String708766606;
typedef unsigned char bool;
void* malloc(unsigned int);
void* calloc(unsigned int, unsigned int);
void free(void*);
void exit(int);
void panic(struct class_std_String708766606*);
void print(const char*);
void println(const char*);
void print_s(struct class_std_String708766606*);
void println_s(struct class_std_String708766606*);
static const void* nullptr = 0;
struct class_std_Object577384124;

struct class_std_Object577384124_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals1391760489) (void*, struct class_std_Object577384124*);
	void (*drop115478603) (void*);
	struct class_std_String708766606* (*toString1664535608) (void*);
};
struct class_std_Object577384124 {
	struct class_std_Object577384124_vtable* __vtable;
	struct class_std_ClassInfo215860611* info;
	long int references;
	struct class_std_ClassInfo215860611* (*getClass2062954782) (void*);
};

struct class_std_ClassInfo215860611* std_Object_getClass216694119(void*);
int std_Object_hashcode1585224288(void*);
bool std_Object_equals454500174(void*, struct class_std_Object577384124*);
void std_Object_drop1730782060(void*);
struct class_std_String708766606* std_Object_toString784171025(void*);

struct class_std_Object577384124* construct_std_Object0_317807437(void*);

static struct class_std_Object577384124* class_std_Object577384124_init1097101603() {
    struct class_std_Object577384124* output;
    output = calloc(1, sizeof(struct class_std_Object577384124));
    struct class_std_Object577384124_vtable* __vtable;
    __vtable = malloc(sizeof(struct class_std_Object577384124_vtable));
    (*output).__vtable = __vtable;
    (*__vtable).offset = 0;
    (*__vtable).hashcode261036375 = std_Object_hashcode1585224288;
    (*__vtable).equals1391760489 = std_Object_equals454500174;
    (*__vtable).drop115478603 = std_Object_drop1730782060;
    (*__vtable).toString1664535608 = std_Object_toString784171025;
    (*output).getClass2062954782 = std_Object_getClass216694119;
    (*output).info = (struct class_std_ClassInfo215860611*) __get_class(0);
    (*output).references = (long int) {0};
    return output;
}



struct class_std_ClassInfo215860611;

struct class_std_ClassInfo215860611_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals1391760489) (void*, struct class_std_Object577384124*);
	void (*drop115478603) (void*);
	struct class_std_String708766606* (*toString1664535608) (void*);
	bool (*equals1225076135) (void*, struct class_std_ClassInfo215860611*);
};
struct class_std_ClassInfo215860611 {
	struct class_std_ClassInfo215860611_vtable* __vtable;
	struct class_std_ClassInfo215860611* info;
	long int references;
	struct class_std_ClassInfo215860611* (*getClass2062954782) (void*);
	struct class_std_String708766606* name;
	struct class_std_ClassInfo215860611* parent;
	int classHash;
	struct class_std_String708766606* (*getName37078109) (void*);
	bool (*is_object2127914142) (void*, struct class_std_Object577384124*);
	bool (*is_class1664696309) (void*, struct class_std_ClassInfo215860611*);
};

struct class_std_String708766606* std_ClassInfo_getName493033082(void*);
bool std_ClassInfo_is_object1597802951(void*, struct class_std_Object577384124*);
bool std_ClassInfo_is_class2100159796(void*, struct class_std_ClassInfo215860611*);
int std_Object_hashcode1585224288(void*);
bool std_ClassInfo_equals861649298(void*, struct class_std_Object577384124*);
void std_Object_drop1730782060(void*);
struct class_std_String708766606* std_Object_toString784171025(void*);
bool std_ClassInfo_equals694964944(void*, struct class_std_ClassInfo215860611*);

struct class_std_ClassInfo215860611* construct_std_ClassInfo0_219382265(void*);

static struct class_std_ClassInfo215860611* class_std_ClassInfo215860611_init498330723() {
    struct class_std_ClassInfo215860611* output;
    output = calloc(1, sizeof(struct class_std_ClassInfo215860611));
    struct class_std_ClassInfo215860611_vtable* __vtable;
    __vtable = malloc(sizeof(struct class_std_ClassInfo215860611_vtable));
    (*output).__vtable = __vtable;
    (*__vtable).offset = 0;
    (*__vtable).hashcode261036375 = std_Object_hashcode1585224288;
    (*__vtable).equals1391760489 = std_ClassInfo_equals861649298;
    (*__vtable).drop115478603 = std_Object_drop1730782060;
    (*__vtable).toString1664535608 = std_Object_toString784171025;
    (*__vtable).equals1225076135 = std_ClassInfo_equals694964944;
    (*output).getClass2062954782 = std_Object_getClass216694119;
    (*output).getName37078109 = std_ClassInfo_getName493033082;
    (*output).is_object2127914142 = std_ClassInfo_is_object1597802951;
    (*output).is_class1664696309 = std_ClassInfo_is_class2100159796;
    (*output).info = (struct class_std_ClassInfo215860611*) __get_class(1);
    (*output).references = (long int) {0};
    (*output).name = (struct class_std_String708766606*) {0};
    (*output).parent = (struct class_std_ClassInfo215860611*) {0};
    (*output).classHash = (int) {0};
    return output;
}





static // bool super_std_Object_equals454500174 (std::Object*)
bool super_std_Object_equals4545001742135757981(void* __this, struct class_std_Object577384124* other) {
    struct class_std_ClassInfo215860611* this = __this;
    struct class_std_Object577384124* super = __this;
    bool (*old) (void*, struct class_std_Object577384124*);
    old = (*(*this).__vtable).equals1391760489;
    (*(*this).__vtable).equals1391760489 = std_Object_equals454500174;
    unsigned char output;
    output = (*(*this).__vtable).equals1391760489(__this, other);
    (*(*this).__vtable).equals1391760489 = old;
    return output;
}

struct class_std_String708766606;

struct class_std_String708766606_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals1391760489) (void*, struct class_std_Object577384124*);
	void (*drop115478603) (void*);
	struct class_std_String708766606* (*toString1664535608) (void*);
};
struct class_std_String708766606 {
	struct class_std_String708766606_vtable* __vtable;
	struct class_std_ClassInfo215860611* info;
	long int references;
	struct class_std_ClassInfo215860611* (*getClass2062954782) (void*);
	char* backingPtr;
	int length;
	struct class_std_String708766606* (*concat939353723) (void*, struct class_std_String708766606*);
	struct class_std_String708766606* (*concat1663899886) (void*, char*);
	struct class_std_String708766606* (*concat_integer631888426) (void*, long int);
	const char* (*getCStr36737184) (void*);
};

struct class_std_String708766606* std_String_concat1418625038(void*, struct class_std_String708766606*);
struct class_std_String708766606* std_String_concat694078875(void*, char*);
struct class_std_String708766606* std_String_concat_integer1305100109(void*, long int);
const char* std_String_getCStr1900251351(void*);
int std_Object_hashcode1585224288(void*);
bool std_Object_equals454500174(void*, struct class_std_Object577384124*);
void std_String_drop1821509932(void*);
struct class_std_String708766606* std_Object_toString784171025(void*);

struct class_std_String708766606* construct_std_String342477975_253337238(void*, const char*);
struct class_std_String708766606* construct_std_String0_1518088663(void*);

static struct class_std_String708766606* class_std_String708766606_init707239644() {
    struct class_std_String708766606* output;
    output = calloc(1, sizeof(struct class_std_String708766606));
    struct class_std_String708766606_vtable* __vtable;
    __vtable = malloc(sizeof(struct class_std_String708766606_vtable));
    (*output).__vtable = __vtable;
    (*__vtable).offset = 0;
    (*__vtable).hashcode261036375 = std_Object_hashcode1585224288;
    (*__vtable).equals1391760489 = std_Object_equals454500174;
    (*__vtable).drop115478603 = std_String_drop1821509932;
    (*__vtable).toString1664535608 = std_Object_toString784171025;
    (*output).getClass2062954782 = std_Object_getClass216694119;
    (*output).concat939353723 = std_String_concat1418625038;
    (*output).concat1663899886 = std_String_concat694078875;
    (*output).concat_integer631888426 = std_String_concat_integer1305100109;
    (*output).getCStr36737184 = std_String_getCStr1900251351;
    (*output).info = (struct class_std_ClassInfo215860611*) __get_class(2);
    (*output).references = (long int) {0};
    (*output).backingPtr = (char*) {0};
    (*output).length = (int) {0};
    return output;
}






static // void super_std_Object_drop1730782060 ()
void super_std_Object_drop17307820602030624382(void* __this) {
    struct class_std_String708766606* this = __this;
    struct class_std_Object577384124* super = __this;
    void (*old) (void*);
    old = (*(*this).__vtable).drop115478603;
    (*(*this).__vtable).drop115478603 = std_Object_drop1730782060;
    (*(*this).__vtable).drop115478603(__this);
    (*(*this).__vtable).drop115478603 = old;
}

struct class_ll_Node1779081790;

struct class_ll_Node1779081790_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals1391760489) (void*, struct class_std_Object577384124*);
	void (*drop115478603) (void*);
	struct class_std_String708766606* (*toString1664535608) (void*);
};
struct class_ll_Node1779081790 {
	struct class_ll_Node1779081790_vtable* __vtable;
	struct class_std_ClassInfo215860611* info;
	long int references;
	struct class_std_ClassInfo215860611* (*getClass2062954782) (void*);
	void* value_ptr;
	struct class_ll_Node1779081790* prev;
	struct class_ll_Node1779081790* next;
	void* (*get_value_ptr138147117) (void*);
	void (*set_value_ptr232255747) (void*, void*);
};

void* ll_Node_get_value_ptr81905700(void*);
void ll_Node_set_value_ptr288497164(void*, void*);
int std_Object_hashcode1585224288(void*);
bool std_Object_equals454500174(void*, struct class_std_Object577384124*);
void std_Object_drop1730782060(void*);
struct class_std_String708766606* std_Object_toString784171025(void*);

struct class_ll_Node1779081790* construct_ll_Node112386326_1603419381(void*, void*);

static struct class_ll_Node1779081790* class_ll_Node1779081790_init1291761683() {
    struct class_ll_Node1779081790* output;
    output = calloc(1, sizeof(struct class_ll_Node1779081790));
    struct class_ll_Node1779081790_vtable* __vtable;
    __vtable = malloc(sizeof(struct class_ll_Node1779081790_vtable));
    (*output).__vtable = __vtable;
    (*__vtable).offset = 0;
    (*__vtable).hashcode261036375 = std_Object_hashcode1585224288;
    (*__vtable).equals1391760489 = std_Object_equals454500174;
    (*__vtable).drop115478603 = std_Object_drop1730782060;
    (*__vtable).toString1664535608 = std_Object_toString784171025;
    (*output).getClass2062954782 = std_Object_getClass216694119;
    (*output).get_value_ptr138147117 = ll_Node_get_value_ptr81905700;
    (*output).set_value_ptr232255747 = ll_Node_set_value_ptr288497164;
    (*output).info = (struct class_std_ClassInfo215860611*) __get_class(6);
    (*output).references = (long int) {0};
    (*output).value_ptr = (void*) {0};
    (*output).prev = (struct class_ll_Node1779081790*) {0};
    (*output).next = (struct class_ll_Node1779081790*) {0};
    return output;
}





struct class_ll_Node1779081790* construct_ll_Node112386326_1603419381(void* __this, void* value_ptr) {
    struct class_ll_Node1779081790* this = (struct class_ll_Node1779081790*) __this;
    (*this).value_ptr = value_ptr;
    return this;
}

struct class_ll_IntLinkedList637343526;

struct class_ll_IntLinkedList637343526_vtable {
	int offset;
	int (*hashcode261036375) (void*);
	bool (*equals1391760489) (void*, struct class_std_Object577384124*);
	void (*drop115478603) (void*);
	struct class_std_String708766606* (*toString1664535608) (void*);
};
struct class_ll_IntLinkedList637343526 {
	struct class_ll_IntLinkedList637343526_vtable* __vtable;
	struct class_std_ClassInfo215860611* info;
	long int references;
	struct class_std_ClassInfo215860611* (*getClass2062954782) (void*);
	struct class_ll_Node1779081790* head;
	struct class_ll_Node1779081790* tail;
	int size;
	void (*add1971166796) (void*, int);
	int (*get1971160983) (void*, int);
	int (*size115917149) (void*);
	bool (*remove1389093271) (void*, int);
	bool (*remove_nth_element157255927) (void*, int);
	struct class_ll_Node1779081790* (*get_nth_node1776022683) (void*, int);
};

void ll_IntLinkedList_add1898726595(void*, int);
int ll_IntLinkedList_get1898720782(void*, int);
int ll_IntLinkedList_size188357350(void*);
bool ll_IntLinkedList_remove1461533472(void*, int);
bool ll_IntLinkedList_remove_nth_element229696128(void*, int);
struct class_ll_Node1779081790* ll_IntLinkedList_get_nth_node1848462884(void*, int);
int std_Object_hashcode1585224288(void*);
bool std_Object_equals454500174(void*, struct class_std_Object577384124*);
void std_Object_drop1730782060(void*);
struct class_std_String708766606* std_Object_toString784171025(void*);

struct class_ll_IntLinkedList637343526* construct_ll_IntLinkedList0_1489565225(void*);

static struct class_ll_IntLinkedList637343526* class_ll_IntLinkedList637343526_init1029551231() {
    struct class_ll_IntLinkedList637343526* output;
    output = calloc(1, sizeof(struct class_ll_IntLinkedList637343526));
    struct class_ll_IntLinkedList637343526_vtable* __vtable;
    __vtable = malloc(sizeof(struct class_ll_IntLinkedList637343526_vtable));
    (*output).__vtable = __vtable;
    (*__vtable).offset = 0;
    (*__vtable).hashcode261036375 = std_Object_hashcode1585224288;
    (*__vtable).equals1391760489 = std_Object_equals454500174;
    (*__vtable).drop115478603 = std_Object_drop1730782060;
    (*__vtable).toString1664535608 = std_Object_toString784171025;
    (*output).getClass2062954782 = std_Object_getClass216694119;
    (*output).add1971166796 = ll_IntLinkedList_add1898726595;
    (*output).get1971160983 = ll_IntLinkedList_get1898720782;
    (*output).size115917149 = ll_IntLinkedList_size188357350;
    (*output).remove1389093271 = ll_IntLinkedList_remove1461533472;
    (*output).remove_nth_element157255927 = ll_IntLinkedList_remove_nth_element229696128;
    (*output).get_nth_node1776022683 = ll_IntLinkedList_get_nth_node1848462884;
    (*output).info = (struct class_std_ClassInfo215860611*) __get_class(7);
    (*output).references = (long int) {0};
    (*output).head = (struct class_ll_Node1779081790*) {0};
    (*output).tail = (struct class_ll_Node1779081790*) {0};
    (*output).size = (int) {0};
    return output;
}









struct class_ll_IntLinkedList637343526* construct_ll_IntLinkedList0_1489565225(void* __this) {
    struct class_ll_IntLinkedList637343526* this = (struct class_ll_IntLinkedList637343526*) __this;
    (*this).head = 0;
    (*this).tail = 0;
    return this;
}

bool boolean_test();
// void* ll::Node::get_value_ptr ()
void* ll_Node_get_value_ptr81905700(void* __this) {
    struct class_ll_Node1779081790* this = __this;
    struct class_std_Object577384124* super = __this;
    return (*this).value_ptr;
}

// void ll::Node::set_value_ptr (void*)
void ll_Node_set_value_ptr288497164(void* __this, void* val) {
    struct class_ll_Node1779081790* this = __this;
    struct class_std_Object577384124* super = __this;
    (*this).value_ptr = val;
}

// void ll::IntLinkedList::add (int)
void ll_IntLinkedList_add1898726595(void* __this, int element) {
    struct class_ll_IntLinkedList637343526* this = __this;
    struct class_std_Object577384124* super = __this;
    struct class_ll_Node1779081790* n = (*this).get_nth_node1776022683(this, ((*this).size-1));
    int* val = malloc(sizeof(int));
    (*val) = element;
    struct class_ll_Node1779081790* next = construct_ll_Node112386326_1603419381(class_ll_Node1779081790_init1291761683(), val);
    (*n).next = next;
    (*next).prev = n;
    ++(*this).size;
}

// ll::Node* ll::IntLinkedList::get_nth_node (int)
struct class_ll_Node1779081790* ll_IntLinkedList_get_nth_node1848462884(void* __this, int n) {
    struct class_ll_IntLinkedList637343526* this = __this;
    struct class_std_Object577384124* super = __this;
    struct class_ll_Node1779081790* ptr = (*this).head;
    for (int i = 0;(i<n); i++){
        ptr = (*ptr).next;
    }


    return ptr;
}

// int ll::IntLinkedList::get (int)
int ll_IntLinkedList_get1898720782(void* __this, int index) {
    struct class_ll_IntLinkedList637343526* this = __this;
    struct class_std_Object577384124* super = __this;
    return (*(int*) ({
    	struct class_ll_Node1779081790* __temp = (*this).get_nth_node1776022683(this, index);
    	(*__temp).get_value_ptr138147117(__temp);
    }));
}

// int ll::IntLinkedList::size ()
int ll_IntLinkedList_size188357350(void* __this) {
    struct class_ll_IntLinkedList637343526* this = __this;
    struct class_std_Object577384124* super = __this;
    return (*this).size;
}

// bool ll::IntLinkedList::remove_nth_element (int)
bool ll_IntLinkedList_remove_nth_element229696128(void* __this, int n) {
    struct class_ll_IntLinkedList637343526* this = __this;
    struct class_std_Object577384124* super = __this;
    if (((n<0)||(n>=(*this).size115917149(this)))) return ((bool) 0);
    struct class_ll_Node1779081790* ptr = (*this).head;
    for (int i = 0;(i<n); i++){
        ptr = (*ptr).next;
    }


    struct class_ll_Node1779081790* prev = (*ptr).prev;
    struct class_ll_Node1779081790* next = (*ptr).next;

    if ((prev!=nullptr)) {
        (*prev).next = next;
    } else {
        (*this).head = next;
    }





    if ((next!=nullptr)) {
        (*next).prev = prev;
    } else {
        (*this).tail = prev;
    }






    --(*this).size;
    return (!(bool) 0);
}

// bool ll::IntLinkedList::remove (int)
bool ll_IntLinkedList_remove1461533472(void* __this, int value) {
    struct class_ll_IntLinkedList637343526* this = __this;
    struct class_std_Object577384124* super = __this;
    for (int i = 0;(i<(*this).size115917149(this)); ++i){
        struct class_ll_Node1779081790* n = (*this).get_nth_node1776022683(this, i);
        if (((*(int*) (*n).get_value_ptr138147117(n))==value)) {
            return (*this).remove_nth_element157255927(this, i);
        }
    }
    return ((bool) 0);
}

bool boolean_test() {
    unsigned char t = !(unsigned char) 0;
    unsigned char f = (unsigned char) 0;
    if ((t!=(!(bool) 0))) {
        return ((bool) 0);
    }


    if ((f!=((bool) 0))) {
        return ((bool) 0);
    }
    return (!(bool) 0);
}

