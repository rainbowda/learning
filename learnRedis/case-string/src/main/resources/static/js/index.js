new Vue({
    el: '#app',
    data () {
        return {
            logContent:"",
            logData:[]
        }
    },
    methods: {
        init(){
            this.logContent = "";
            this.getMyLog();
        },
        getMyLog(){
            axios.get('/myLog/getMyLog')
                .then(res=>{ this.logData = res.data;})
                .catch(err=>{ console.log(err) })
        },
        selectMenuItem(menuItemName){
            if(menuItemName == "addMyLog")
                this.addMyLog();
        },
        addMyLog() {
            this.$Modal.confirm({
                render: (h) => {
                    return h('Input', {
                        props: {
                            value: this.value,
                            autofocus: true,
                            placeholder: "请输入日志内容",
                            type: "textarea",
                            rows: 4
                        },
                        on: {
                            input: (val) => {
                               this.logContent = val;
                            }
                        }
                    })
                },
                onOk:() =>{
                    if(this.logContent.trim() == ""){
                        this.$Notice.warning({
                            desc:'日志内容不能为空!'
                        });
                        return;
                    }

                    //
                    axios.post('/myLog/addMyLog',{logContent:this.logContent})
                         .then(res=>{
                             if(res.data == true){
                                 this.$Notice.success({
                                     desc: '添加日志成功'
                                 });
                             } else {
                                 this.$Notice.warning({
                                     desc: '添加日志失败'
                                 });
                             }
                             this.init();
                         })
                }
            });

        },
        updateMyLog(item){
            console.log(item);
            var obj = JSON.parse(item);

            this.$Modal.confirm({
                render: (h) => {
                    return h('Input', {
                        props: {
                            value: this.value,
                            autofocus: true,
                            placeholder: "请输入日志内容",
                            type: "textarea",
                            rows: 4,
                            value: obj.logContent
                        },
                        on: {
                            input: (val) => {
                                obj.logContent = val;
                            }
                        }
                    })
                },
                onOk:() =>{
                    if(obj.logContent.trim() == ""){
                        this.$Notice.warning({
                            desc:'日志内容不能为空!'
                        });
                        return;
                    }

                    //
                    axios.post('/myLog/updateMyLog',obj)
                        .then(res=>{
                            if(res.data == true){
                                this.$Notice.success({
                                    desc: '修改日志成功'
                                });
                            } else {
                                this.$Notice.warning({
                                    desc: '修改日志失败'
                                });
                            }
                            this.init();
                        })
                }
            });
        },
        delMyLog(id){
            this.$Modal.confirm({
                title: '删除',
                content: '你确定要删除这条记录？',
                onOk: () => {
                    axios.delete('/myLog/delMyLog/'+id)
                        .then(res=>{
                            if(res.data == true){
                                this.$Notice.success({
                                    desc: '删除日志成功'
                                });
                            } else {
                                this.$Notice.warning({
                                    desc:'删除日志失败'
                                });
                            }
                            this.init();
                        })
                }
            });
        },
    },
    mounted(){
        this.init();
    }
})