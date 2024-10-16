// src/main/resources/static/app.js
new Vue({
    el: '#app',
    data: {
        contacts: [],
        contact: { name: '', address: '', phone: '' },
        editing: false
    },
    methods: {
        loadContacts() {
            axios.get('/api/contacts').then(response => {
                this.contacts = response.data;
            });
        },
        saveContact() {
            if (this.editing) {
                axios.put(`/api/contacts/${this.contact.id}`, this.contact).then(() => {
                    this.loadContacts();
                    this.resetForm();
                });
            } else {
                axios.post('/api/contacts', this.contact).then(() => {
                    this.loadContacts();
                    this.resetForm();
                });
            }
        },
        editContact(contact) {
            this.contact = {...contact};
            this.editing = true;
        },
        deleteContact(id) {
            axios.delete(`/api/contacts/${id}`).then(() => {
                this.loadContacts();
            });
        },
        resetForm() {
            this.contact = { name: '', address: '', phone: '' };
            this.editing = false;
        }
    },
    mounted() {
        this.loadContacts();
    }
});