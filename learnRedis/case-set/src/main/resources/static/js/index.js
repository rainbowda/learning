new Vue({
    el: '#app',
    data () {
        return {
            aFriendData: [],
            bFriendData: [],
            resultFrient:[],
            modalVisible:false,
            submitLoading:false,
            resultFrientTitle:"",
            form:{
                user:"",
                friend:""
            },
            formValidate:{
                user: [
                    { required: true, message: '请选择用户', trigger: 'blur' }
                ],
                friend: [
                    { required: true, message: '请输入好友名称', trigger: 'blur' }
                ],
            },
        }
    },
    methods: {
        init(){
            this.resultFrientTitle = "";
            this.getList();
        },
        getList(){
            axios.get('/set/getList')
                .then(res=>{
                    this.aFriendData = res.data.aFriend;
                    this.bFriendData = res.data.bFriend;
                })
        },
        addFriend(){
            this.modalVisible = true;
            this.form.friend = "";
        },
        cancel(){
            this.modalVisible = false;
        },
        submit(){
            this.$refs.form.validate(valid => {
                if (valid) {
                    this.submitLoading = true;
                    var params = new URLSearchParams();
                    params.append('user', this.form.user);
                    params.append('friend', this.form.friend);

                    axios.post('/set/addFriend',params)
                        .then(res=>{
                            this.submitLoading = false;
                            if(res.data > 0){
                                this.$Notice.success({
                                    desc: '添加好友成功'
                                });
                                this.modalVisible = false;
                                this.form.data = "";
                            } else {
                                this.$Notice.warning({
                                    desc: '已经有该好友'
                                });
                            }
                            this.init();
                        })
                }
            });
        },
        delFriend(user, friend){
            this.$Modal.confirm({
                title: '删除',
                content: '你确定要删除'+friend+"?",
                onOk: () => {
                    axios.delete('/set/delFriend',{params:{"user":user,"friend":friend}})
                        .then(res=>{
                            if(res.data > 0){
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
        intersectFriend(){
            this.resultFrientTitle = "共同好友";
            axios.get('/set/intersectFriend')
                .then(res=>{
                    this.resultFrient = res.data;
                })
        },
        differenceFriend(){
            this.resultFrientTitle = "用户独有的好友";
            axios.get('/set/differenceFriend')
                .then(res=>{
                    this.resultFrient = res.data;
                })
        },
        unionFriend(){
            this.resultFrientTitle = "所有的好友";
            axios.get('/set/unionFriend')
                .then(res=>{
                    this.resultFrient = res.data;
                })
        },
    },
    mounted(){
        this.init();
    }
})