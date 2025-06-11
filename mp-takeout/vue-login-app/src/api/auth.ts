import request from '../utils/request';
import type {
    AuthLoginDTO,
    AuthLoginVo,
    KaptchaVO
} from './types';

export const login = (data: AuthLoginDTO) =>
  request.post<AuthLoginVo>('/auth/login', data);

export const getCaptchaBase64 = () =>
  request.get<KaptchaVO>('/auth/base64-captcha');
