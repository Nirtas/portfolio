const express = require('express')
const exphbs = require('express-handlebars')
const siteRoutes = require('./routes/routes')
const path = require('path')
const cookieParser = require('cookie-parser')


const app = express()
const PORT = process.env.PORT || 3000
const hbs = exphbs.create({
    defaultLayout: 'main',
    extname: 'hbs'
})

app.engine('hbs', hbs.engine)
app.set('view engine', 'hbs')
app.set('views', 'views')

app.use(express.urlencoded({ extended: true }))
app.use(express.static(path.join(__dirname, 'public')))
app.use(cookieParser('secret'))
app.use(siteRoutes)


async function StartServer() {
    try {
        app.listen(PORT, () => {
            console.log(`Server has been started on ${PORT}...`)
        })

    } catch (e) {
        console.log(e)
    }
}

StartServer()