import groovy.sql.Sql
import groovy.yaml.YamlSlurper

import java.time.LocalDate

class Main {

    static void main(String[] args) {
        println('Application start')

        def path = "${System.getProperty("user.home")}/registries_${LocalDate.now()}/"
        new File(path).mkdirs()

        def props = getProps()

        def sql = Sql.newInstance(props.url, props.username, props.password, props.className)

        def rows = sql.rows("select * from mir.POS_REGISTRY_FILES prf where prf.PROGRAM_TYPE = 'KFC' and prf.FDAY between to_date('${props.fromDate}', 'yyyy-mm-dd') and to_date('${props.toDate}', 'yyyy-mm-dd')")

        println('Rows size = ' + rows.size())

        rows.each {
            def filename = it.get('FILENAME') as String
            def clob = it.get('FILEDATA') as java.sql.Clob

            def file = new File(path + filename)
            file.createNewFile()
            file.write(clob.characterStream.text)

            println("${filename} successfully saved")
        }

        println('Application end')
    }

    static Props getProps() {
        def ys = new YamlSlurper()
        Props props = ys.parse(getClass().getResource('/props.yml').newInputStream()) as Props

        return props
    }

    static class Props {
        private String url
        private String username
        private String password
        private String className
        private String fromDate
        private String toDate
    }
}
