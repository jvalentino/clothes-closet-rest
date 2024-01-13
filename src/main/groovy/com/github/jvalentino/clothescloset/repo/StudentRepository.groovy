package com.github.jvalentino.clothescloset.repo

import com.github.jvalentino.clothescloset.entity.Student
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * DAO for student
 * @author john.valentino
 */
interface StudentRepository extends JpaRepository<Student, String> {

    @Query(value = '''
            select s.shoe_size, count(*) as records
            from
                appointment a,
                visit v,
                student s
            where
                a.happened = false
                and v.appointment_id = a.appointment_id
                and s.student_id = v.student_id
                and a.datetime >= ?1 and a.datetime < ?2
            group by s.shoe_size
            order by s.shoe_size desc
        ''', nativeQuery = true)
    List<Object[]> reportOnShoeSizes(Date startDate, Date endDate)

    @Query(value = '''
            select s.underwear_size, count(*) as records
            from
                appointment a,
                visit v,
                student s
            where
                a.happened = false
                and v.appointment_id = a.appointment_id
                and s.student_id = v.student_id
                and a.datetime >= ?1 and a.datetime < ?2
            group by s.underwear_size
            order by s.underwear_size desc
        ''', nativeQuery = true)
    List<Object[]> reportOnUnderwearSizes(Date startDate, Date endDate)

}
