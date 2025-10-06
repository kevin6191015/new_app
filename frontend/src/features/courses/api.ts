import type { Course } from './types'
 import http from '@shared/api/http'


 export async function getSemesters(): Promise<string[]> {
   const { data } = await http.get<string[]>('/courses/semesters')
   return data ?? []
 }

 export async function getAllCourses(): Promise<Course[]> {
   const { data } = await http.get<Course[]>('/courses')
   return data ?? []
 }

 export async function getCoursesBySemester(sem: string): Promise<Course[]> {
   const { data } = await http.get<Course[]>(`/courses`, { params: { sem } })
   return data ?? []
 }
