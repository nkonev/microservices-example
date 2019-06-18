<template>
    <v-app>
        <v-navigation-drawer
                absolute
                temporary
                v-model="drawer"
                left
                app
        >
            <v-list dense>
                <v-list-tile @click="">
                    <v-list-tile-action>
                        <v-icon>home</v-icon>
                    </v-list-tile-action>
                    <v-list-tile-content>
                        <v-list-tile-title>Home</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>
                <v-list-tile @click="">
                    <v-list-tile-action>
                        <v-icon>contact_mail</v-icon>
                    </v-list-tile-action>
                    <v-list-tile-content>
                        <v-list-tile-title>Contact</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>
            </v-list>
        </v-navigation-drawer>

        <v-toolbar app dark class="primary">
            <v-toolbar-side-icon @click.stop="drawer = !drawer" class="hidden-md-and-up"></v-toolbar-side-icon>
            <v-toolbar-title v-text="'Admin App'"></v-toolbar-title>
            <v-spacer/>
            <v-toolbar-items class="hidden-sm-and-down">
                <v-btn color="success">Success</v-btn>
                <v-btn flat>
                    <v-icon left v-html="'visibility'"></v-icon>
                    Users222
                </v-btn>
            </v-toolbar-items>
        </v-toolbar>
        <v-content>
            <v-container fluid fill-height>
                <v-layout justify-center align-center>
                    <v-flex text-xs-center>
                        <v-btn fab dark small color="primary">
                            <v-icon dark>remove</v-icon>
                        </v-btn>

                        <v-btn fab dark small color="pink">
                            <v-icon dark>favorite</v-icon>
                        </v-btn>

                        <v-btn fab dark color="indigo">
                            <v-icon dark>add</v-icon>
                        </v-btn>

                        <v-btn fab dark color="teal">
                            <v-icon dark>list</v-icon>
                        </v-btn>

                        <v-btn fab dark large color="cyan">
                            <v-icon dark>edit</v-icon>
                        </v-btn>

                        <v-btn fab dark large color="purple">
                            <v-icon dark>android</v-icon>
                        </v-btn>
                    </v-flex>

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
                </v-layout>
            </v-container>
        </v-content>
        <v-footer app>(C) Cool app</v-footer>
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
                drawer: false,
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
