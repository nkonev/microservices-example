<template>
    <v-app>
        <v-navigation-drawer
                absolute
                v-model="drawer"
                left
                app
        >
            <v-list dense>
                <template v-for="(item, index) in items">
                    <v-list-tile :key="index" ripple @click="">
                        <v-list-tile-content>
                            <v-list-tile-title>{{ item.title }}</v-list-tile-title>
                        </v-list-tile-content>
                    </v-list-tile>
                </template>
            </v-list>
        </v-navigation-drawer>

        <v-toolbar app dark class="primary">
            <v-toolbar-side-icon @click.stop="drawer = !drawer" class="md-and-up"></v-toolbar-side-icon>
            <v-btn color="success"
                   @click.stop="newChatDialog = true">New chat</v-btn>
            <v-toolbar-title v-text="'Chat App'"></v-toolbar-title>
            <v-spacer/>
            <v-toolbar-items class="hidden-sm-and-down">
                <v-btn flat>
                    <v-layout row fill-height>
                        <v-flex flexbox>
                            <v-avatar
                                    class="mr-2"
                                    color="grey lighten-4"
                                    title="nkonev"
                            >
                                <v-img src="https://vuetifyjs.com/apple-touch-icon-180x180.png" alt="avatar"/>
                            </v-avatar>
                            <span>John Doe</span>
                        </v-flex>
                    </v-layout>
                </v-btn>
            </v-toolbar-items>
        </v-toolbar>
        <v-content>
            <v-layout row justify-center>
                <v-dialog v-model="newChatDialog" persistent max-width="600px">
                    <v-card>
                        <v-card-title>
                            <span class="headline">Create chat</span>
                        </v-card-title>
                        <v-card-text>
                            <v-container grid-list-md>
                                <v-layout wrap>
                                    <v-flex grow>
                                        <v-text-field label="Chat name*" required></v-text-field>
                                    </v-flex>
                                </v-layout>
                            </v-container>
                            <small>*indicates required field</small>
                        </v-card-text>
                        <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn color="blue darken-1" outline @click="newChatDialog = false">Close</v-btn>
                            <v-btn color="success" @click="newChatDialog = false">Save</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-dialog>
            </v-layout>
            <v-flex>
                    <v-list two-line class="elevation-12">
                        <template v-for="(item, index) in items">
                            <v-list-tile :key="index" avatar ripple @click="">
                                <v-list-tile-content>
                                    <v-list-tile-title>{{ item.title }}</v-list-tile-title>
                                    <v-list-tile-sub-title class="text--primary">{{ item.headline }}</v-list-tile-sub-title>
                                    <v-list-tile-sub-title>{{ item.subtitle }}</v-list-tile-sub-title>
                                </v-list-tile-content>
                                <v-list-tile-action>
                                    <v-list-tile-action-text>{{ item.action }}</v-list-tile-action-text>
                                    <v-icon color="grey lighten-1">star_border</v-icon>
                                </v-list-tile-action>
                            </v-list-tile>
                            <v-divider v-if="index + 1 < items.length" :key="`divider-${index}`"></v-divider>
                        </template>
                        <v-input
                                :messages="['Messages']"
                                append-icon="close"
                                prepend-icon="phone"
                        >
                            Default Slot
                        </v-input>
                    </v-list>
            </v-flex>
            <v-footer app>
                <v-spacer></v-spacer>
                <div class="mr-2">&copy; {{ new Date().getFullYear() }}</div>
            </v-footer>
        </v-content>
    </v-app>
</template>

<script>
    import Vue from 'vue'
    import Vuetify from 'vuetify'
    import 'material-design-icons-iconfont/dist/material-design-icons.css'

    Vue.use(Vuetify);

    export default {
        data(){
            return {
                newChatDialog: false,
                drawer: true,
                items: [
                    { action: '15 min', headline: 'Brunch this weekend?', title: 'Ali Connors', subtitle: "I'll be in your neighborhood doing errands this weekend. Do you want to hang out?" },
                    { action: '2 hr', headline: 'Summer BBQ', title: 'me, Scrott, Jennifer', subtitle: "Wish I could come, but I'm out of town this weekend." },
                    { action: '6 hr', headline: 'Oui oui', title: 'Sandra Adams', subtitle: 'Do you have Paris recommendations? Have you ever been?' },
                    { action: '12 hr', headline: 'Birthday gift', title: 'Trevor Hansen', subtitle: 'Have any ideas about what we should get Heidi for her birthday?' },
                    { action: '18hr', headline: 'Recipe to try', title: 'Britta Holt', subtitle: 'We should eat this: Grate, Squash, Corn, and tomatillo Tacos.' }
                ]
            }
        },
        name: 'AdminApp',
    }
</script>

<style lang="stylus">
    @import '~vuetify/src/stylus/main';
</style>

<style lang="stylus" scoped>
    .application {
        font-family: Arial, sans-serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;


        #input-usage .v-input__prepend-outer,
        #input-usage .v-input__append-outer,
        #input-usage .v-input__slot,
        #input-usage .v-messages {
            border: 1px dashed rgba(0,0,0, .4);
        }
    }
</style>
