new Vue({
    el: '#app',
    data () {
        return {
            title:"",
            modalVisible:false,
            submitLoading:false,
            pushType:"",
            form:{
                data:""
            },
            formValidate:{
                data: [
                    { required: true, message: '请输入内容', trigger: 'blur' }
                ],
            },
            listData:[],
            listDataSize:0,
            popListData:[],
            columns: [
                {
                    title: 'data',
                    key: 'data'
                },
            ],
        }
    },
    methods: {
        init(){
            this.listData = [];
            this.listDataSize = 0;
            this.getList();
        },
        getList(){
            axios.get('/list/getList')
                .then(res=>{
                    this.listData = res.data;
                    this.listDataSize = this.listData.length;
                })
        },
        openLeftPushModel(){
            this.pushType = "leftPush";
            this.modalVisible = true;
        },
        openRightPushModel(){
            this.pushType = "rightPush";
            this.modalVisible = true;
        },
        cancel(){
            this.modalVisible = false;
        },
        submit(){
            this.$refs.form.validate(valid => {
                if (valid) {
                    this.submitLoading = true;
                    axios.post('/list/'+this.pushType,this.form)
                        .then(res=>{
                            this.submitLoading = false;
                            if(res.data == true){
                                this.$Notice.success({
                                    desc: '新增队列成功'
                                });
                                this.modalVisible = false;
                                this.form.data = "";
                            } else {
                                this.$Notice.warning({
                                    desc: '添加队列失败'
                                });
                            }
                            this.init();
                        })
                }
            });
        },
        leftPop(){
            axios.get('/list/leftPop')
                .then(res=>{
                    this.$Message.success('弹出数据：'+JSON.stringify(res.data));
                    this.popListData.push(res.data);
                    this.init();
                })
        },
        rightPop(){
            axios.get('/list/rightPop')
                .then(res=>{
                    this.$Message.success('弹出数据：'+JSON.stringify(res.data));
                    this.popListData.push(res.data);
                    this.init();
                })
        },
    },
    mounted(){
        this.init();
    }
})