package service

import dao.HibernateSessionFactory
import entity.*
import java.time.LocalDateTime
import java.time.YearMonth
import javax.inject.Inject
import javax.persistence.EntityManager
import kotlin.streams.toList

class GroupServiceImpl @Inject constructor(
    private val hibernateFactory: HibernateSessionFactory
) : GroupService  {

    override fun create(workoutType: WorkoutType, period: YearMonth, selectedDates: List<LocalDateTime>, price: Int): Group {
        val entityManager = beginTransaction()

        val group = Group(workoutType, price, period, ArrayList())

        // Костыль для наполнения группы запланированными датами тренировок
        selectedDates.stream()
            .map { date -> Workout(date, group) }
            .sorted { o1, o2 -> o1.date.compareTo(o2.date) }
            .forEach { group.workout.add(it) }


        entityManager.persist(group)
        commitAndClose(entityManager)
        return group
    }

    override fun find(pattern: String): List<Group> {
        TODO("Not yet implemented")
    }

    override fun getAll(): List<Group> {
        val entityManager = hibernateFactory.sessionFactory.createEntityManager();
        val criteria = entityManager.criteriaBuilder.createQuery(Group::class.java)
        criteria.from(Group::class.java)
        return entityManager.createQuery(criteria).resultList
    }

    override fun markWorkout(workout: Workout, visits: List<WorkoutVisit>) {
        TODO("Not yet implemented")
    }

    override fun close(group: Group): Group {
        TODO("Not yet implemented")
    }

    override fun getClientList(group: Group): List<Client> {
        val entityManager = hibernateFactory.sessionFactory.createEntityManager()
        val criteria = entityManager.criteriaBuilder.createQuery(Subscription::class.java)
        criteria.from(Subscription::class.java)
        return entityManager.createQuery(criteria).resultList.stream()
            .filter{ subscription -> subscription.group == group }
            .map(Subscription::client)
            .toList()
    }

    override fun getVisitList(group: Group, client: Client): List<WorkoutVisit> {
        val workouts = group.workout

        val entityManager = hibernateFactory.sessionFactory.createEntityManager();
        val query = entityManager.createQuery(
            "select v from WorkoutVisit v where v.client = :client",
            WorkoutVisit::class.java
        )
        query.setParameter("client", client)
        val visits = query.resultStream
            .filter { workouts.contains(it.workout) }
            .toList()

        entityManager.close()
        return visits
    }

    private fun beginTransaction() : EntityManager {
        val entityManager = hibernateFactory.sessionFactory.createEntityManager();
        entityManager.transaction.begin()
        return entityManager
    }

    private fun commitAndClose(entityManager: EntityManager) {
        entityManager.transaction.commit()
        entityManager.close()
    }
}