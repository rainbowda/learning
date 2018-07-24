new Vue({
    el: '#app',
    data () {
        return {
            sortType:"ASC",
            sortName:"降序显示",
            listData:[],

        }
    },
    methods: {
        init(){
            this.getList();
        },
        getList(){
            axios.get('/zset/getList/'+this.sortType)
                .then(res=>{
                    this.listData = res.data;
                })
        },
        changeSortType(){
            if (this.sortType == "ASC"){
                this.sortType = "DESC";
                this.sortName = "升序显示";
            } else {
                this.sortType = "ASC";
                this.sortName = "降序显示";
            }
            this.getList();
        },
        star(type, member){
            var params = new URLSearchParams();
            params.append('type', type);
            params.append('member', member);

            axios.post('/zset/star',params)
                .then(res=>{
                    this.$Message.success('操作成功');
                    this.init();
                })
        },
        rank(type, member){

            axios.get('/zset/rank/'+type+"/"+member)
                .then(res=>{
                    this.$Message.success(member+'的排名:'+res.data);
                    this.init();
                })
        },
    },
    mounted(){
        this.init();
    }
})