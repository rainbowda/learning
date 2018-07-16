new Vue({
    el: '#app',
    data () {
        return {
            logContext:"",
            logData:[]
        }
    },
    methods: {
        init(){
            this.logContext = "";
            this.getMyLog();
        },
        getMyLog(){
            axios.get('/myLog/getMyLog')
                .then(res=>{ logData = res })
                .catch(err=>{ console.log(err) })
        },
        selectMenuItem(menuItemName){
            if(menuItemName == "addMyLog")
                this.addMyLog();
            else
                this.delMyLog();
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
                               this.logContext = val;
                            }
                        }
                    })
                },
                onOk:() =>{
                    if(this.logContext.trim() == ""){
                        this.$Notice.warning({
                            desc:'日志内容不能为空!'
                        });
                        return;
                    }

                    //
                    var params = new URLSearchParams();
                    params.append('logContext', this.logContext);
                    axios.post('/myLog/addMyLog',params)
                         .then(res=>{ console.log(res) })
                         .catch(err=>{ console.log(err) })

                }
            });

            this.init();
        },
        delMyLog(){
            alert("delMyLog");
            this.init();
        },

    },
    mounted(){
        this.init();
    }
})