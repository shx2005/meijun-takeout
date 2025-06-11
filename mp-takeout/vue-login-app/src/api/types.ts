// Auth 相关类型
export interface AuthLoginDTO {
  username: string;
  password: string;
}

export interface AuthRegisterDTO {
  username: string;
  password: string;
  name: string;
}

export interface MpLoginDTO {
  code: string; // 微信登录凭证
  encryptedData?: string;
  iv?: string;
}

export interface AuthLoginVo {
  id: number;
  username: string;
  name: string;
  uuid: string;
  token: string;
}

export interface KaptchaVO {
  uuid: string;
  code: string;
}

// 用户身份枚举（可选）
export enum UserIdentity {
  ADMIN = 'ADMIN',
  CUSTOMER = 'CUSTOMER',
  EMPLOYEE = 'EMPLOYEE',
  MERCHANT = 'MERCHANT'
}
