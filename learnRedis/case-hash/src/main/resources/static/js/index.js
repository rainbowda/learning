new Vue({
    el: '#app',
    data () {
        return {
            contactsData:[],
            currentItemId:null,
            currentItem:[],
            modalVisible:false,
            viewModalVisible:false,
            submitLoading:false,
            form:{
                name:"",
                phone:"",
            },
            formValidate:{
                name: [
                    { required: true, message: '请输入姓名', trigger: 'blur' }
                ],
                phone: [
                    { required: true, message: '请输入号码', trigger: 'blur' }
                ],
            },
            columns: [
                {
                    title: 'Name',
                    key: 'name'
                },
                {
                    title: 'value',
                    key: 'value'
                },
                {
                    title: '操作',
                    width: 120,
                    render: (h, params) => {
                        return h('div', [
                            h('Button', {
                                props: {
                                    type: 'text',
                                    size: 'small'
                                },
                                on: {
                                    click: () => {
                                        this.delAttr(params.row.name);
                                    }
                                }
                            }, '删除')
                        ]);
                    }
                }
            ],
            attrModalVisible:false,
            attrForm:{
                name:"",
                phone:"",
            },
            attrFormValidate:{
                fieldName: [
                    { required: true, message: '请输入属性名称', trigger: 'blur' }
                ],
                fieldValue: [
                    { required: true, message: '请输入属性值', trigger: 'blur' }
                ],
            },
            submitAttrLoading:false,
        }
    },
    methods: {
        init(){
            this.currentItem = [];
            this.getList();
        },
        getList(){
            axios.get('/contacts/getList')
                .then(res=>{ this.contactsData = res.data;})
        },
        openAddModel(){
            this.modalVisible = true;
            this.form.name = "";
            this.form.phone = "";
        },
        openEditModel(item){
            this.currentItemId = null;
            this.currentItem = [];
            this.viewModalVisible = true;
            for(var i in item){
                if(i == "id"){
                    this.currentItemId = item[i];
                    continue;
                }
                this.currentItem.push({
                        name:i,
                        value:item[i]
                });
            }
        },
        closeViewModal(){
            this.viewModalVisible = false;
            this.init();
        },
        cancel(){
            this.modalVisible = false;
        },
        submit(){
            this.$refs.form.validate(valid => {
                if (valid) {
                    this.submitLoading = true;
                    axios.post('/contacts/add',this.form)
                        .then(res=>{
                            this.submitLoading = false;
                            if(res.data == true){
                                this.$Notice.success({
                                    desc: '新增联系人成功'
                                });
                                this.init();
                                this.modalVisible = false;
                            } else {
                                this.$Notice.warning({
                                    desc: '添加联系人失败'
                                });
                            }
                            this.init();
                        })
                }
            });
        },
        update(){

        },
        del(id){
            this.$Modal.confirm({
                title: '删除',
                content: '你确定要删除这条记录？',
                onOk: () => {
                    axios.delete('/contacts/del/'+id)
                        .then(res=>{
                            if(res.data == true){
                                this.$Notice.success({
                                    desc: '删除成功'
                                });
                            } else {
                                this.$Notice.warning({
                                    desc:'删除失败'
                                });
                            }
                            this.init();
                        })
                }
            });
        },
        delAttr(fieldName){
            if (fieldName == "name" || fieldName == "phone"){
                this.$Message.warning("主要属性不能删除!");
                return;
            }

            axios.post('/contacts/delAttr',{"id":this.currentItemId,"fieldName":fieldName})
                .then(res=>{
                    if(res.data == true){
                        this.$Message.success("删除成功");
                        for(var i in this.currentItem){
                            if(this.currentItem[i].name == fieldName){
                                this.currentItem.splice(i,1);
                            }
                        }
                    }
                })
        },
        openAttrModel(){
            this.attrModalVisible = true;
        },
        cancelAttr(){
            this.attrModalVisible = false;
        },
        submitAttr(){
            this.$refs.attrForm.validate(valid => {
                if (valid) {
                    this.submitAttrLoading = true;
                    this.attrForm.id = this.currentItemId;
                    axios.post('/contacts/addAttr',this.attrForm)
                        .then(res=>{
                            this.submitAttrLoading = false;
                            this.attrModalVisible = false;
                            if(res.data == true){
                                this.$Message.success("添加成功");
                                for(var i in this.currentItem){
                                    if(this.currentItem[i].name == this.attrForm.fieldName){
                                        this.currentItem.splice(i,1);
                                    }
                                }
                                this.currentItem.push({
                                    name:this.attrForm.fieldName,
                                    value:this.attrForm.fieldValue
                                })
                            }

                            this.attrForm.fieldName = "";
                            this.attrForm.fieldValue = "";
                        })
                }
            });
        },
    },
    mounted(){
        this.init();
    }
})