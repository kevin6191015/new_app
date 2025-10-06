import type { AxiosInstance } from 'axios'
import AxiosMockAdapter from 'axios-mock-adapter'
import { mountAuth } from './modules/auth'
import { mountCourses } from './modules/courses'

export function setupMock(http: AxiosInstance) {
  // 預設「只攔你定義的端點，其餘放行到真實後端」
  const mock = new AxiosMockAdapter(http, {
    delayResponse: Number(import.meta.env.VITE_MOCK_LATENCY),
    onNoMatch: 'passthrough'
  })

  mountAuth(mock)
  mountCourses(mock) 

  // 之後若要讓某條改打真後端，可用：
  // mock.onPost(/\/auth\/login$/).passThrough()

  return mock
}
